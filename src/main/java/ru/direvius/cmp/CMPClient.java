package ru.direvius.cmp;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author direvius
 */
public class CMPClient {

    private static final Logger logger = LoggerFactory.getLogger(CMPClient.class);
    
    private final InputStream is;
    private final OutputStream os;
    private static final int ENQ_RESP_LEN = 8;
    private static final byte[] PROTO_VERSION = {0x1, 0x0};
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;
    private static final byte EOT = 0x04;
    private static final byte ENQ = 0x05;
    private static final byte ACK = 0x06;
    private static final byte BEL = 0x07;
    private static final byte NAK = 0x15;
    private static final String TDES_TRANSFORMATION = "DESede/ECB/NoPadding";
    private static final String DES_TRANSFORMATION = "DES/ECB/NoPadding";
    private static final byte[] TERMINAL_KEY = hexStringToByteArray("C3-5F-ED-30-8F-9C-34-F4-E7-E4-83-3C-67-67-13-EC-24-08-0C-3E-34-C8-F7-BD");
    private static final byte[] TRANSPORT_KEY = hexStringToByteArray("EF-B1-CF-18-5A-9E-DB-B7-0E-C7-F8-0A-E7-28-9A-EA-24-45-1A-EB-4E-09-E2-FE");
    private static final SecretKey terminalKey = new SecretKeySpec(TERMINAL_KEY, "DESede");
    private static final SecretKey transportKey = new SecretKeySpec(TRANSPORT_KEY, "DESede");
    private Encrypter sessionEncrypter;
    private byte[] sessionKeyArray;
    private enum State {

        DOWN, ESTABLISHED
    }
    private State state = State.DOWN;
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> keepAliver;
    private SecretKey sessionKey;
    
    private class KeepAliver implements Runnable {
        public void run() {
            try {
                keepAlive();
            } catch (IOException ex) {
                logger.error("IOException while trying to automatically keep the connection alive.", ex);
            }
        }
    }

    public synchronized void keepAlive() throws IOException {
        if(state == State.ESTABLISHED){
            os.write(BEL);
            os.flush();
            int respCode = is.read();
            if(logger.isDebugEnabled())logger.debug("Sent keep-alive, received {}", String.format("0x%02X",respCode));
        }
    }
    public void sendEncrypt(byte[] message) throws IOException, GeneralSecurityException {
        if(message.length%8 == 0){
            send(sessionEncrypter.encrypt(message));
        } else {
            int ordinalPartLength = (message.length / 8) * 8;
            ByteBuffer ordinalPart = ByteBuffer.allocate(ordinalPartLength);
            ByteBuffer lastBytes = ByteBuffer.allocate(message.length - ordinalPartLength);
            ordinalPart.put(message, 0, ordinalPartLength);
            lastBytes.put(message, ordinalPartLength, message.length - ordinalPartLength);
            ByteBuffer encryptedMessage = ByteBuffer.allocate(message.length);
            encryptedMessage.put(sessionEncrypter.decrypt(ordinalPart.array())).put(symmetric(lastBytes.array()));
            send(encryptedMessage.array());
        }
    }
    public synchronized void send(byte[] message) throws IOException {
        if(state!=State.ESTABLISHED) throw new IOException("send operation while connection is down");
        ByteBuffer bb = ByteBuffer.allocate(message.length + 3).putShort((short) message.length).put(message).put(ETX);
        ByteBuffer bb2 = ByteBuffer.allocate(bb.capacity() + 3);
        bb2.put(STX).put(bb.array()).putShort(crc16(bb.array()));
        os.write(bb2.array());
        os.flush();
        logger.debug("Sent a message: {}", byteArrayToString(message));
        int respCode = is.read();
        if(logger.isDebugEnabled())logger.debug("Response code: {}", String.format("0x%02X",respCode));
        switch(respCode){
            case ACK:
                logger.debug("Server responded with ACK");
                break;
            case NAK:
                throw new IOException("Server responded with NAK");
            default:
                throw new IOException("Unknown response code");
        }
    }

    public synchronized void close() throws IOException {
        if(state!=State.ESTABLISHED) throw new IOException("close operation while connection is down");
        os.write(EOT);
        os.flush();
        keepAliver.cancel(true);
        is.close();
        os.close();
        state = State.DOWN;
        logger.debug("Closed connection");
    }

    public synchronized void open() throws IOException {
        if(state==State.ESTABLISHED) return; // already opened
        logger.debug("Opening connection...");
        os.write(ENQ);
        os.write(PROTO_VERSION);
        os.flush();
        logger.debug("Sent ENQ byte and protocol version ({})", PROTO_VERSION);
        if(is.read() == ACK){
            try {
                byte[] buff = new byte[ENQ_RESP_LEN];
                is.read(buff);
                logger.debug("Received a crypted session key: {}", byteArrayToString(buff));
                
                Encrypter tdesTerminalEncrypter = new Encrypter(TDES_TRANSFORMATION,terminalKey);
                Encrypter tdesTransportEncrypter = new Encrypter(TDES_TRANSFORMATION,transportKey);
                
                byte [] decryptedKey = tdesTerminalEncrypter.decrypt(buff);
                logger.debug("Decrypted a key: {}", byteArrayToString(decryptedKey));
                decryptedKey[7] = 0x7;
                sessionKeyArray = tdesTransportEncrypter.encrypt(decryptedKey);
                logger.debug("Encrypted a key (session key): {}", byteArrayToString(sessionKeyArray));
                sessionKey = new SecretKeySpec(sessionKeyArray, "DES");
                sessionEncrypter = new Encrypter(DES_TRANSFORMATION, sessionKey);
                state = State.ESTABLISHED;
                logger.debug("Opened connection");
            } catch (GeneralSecurityException ex) {
                logger.error("Could not make session key cryptography.", ex);
            }
        } else {
            throw new IOException("Could not establish connection");
        }
    }
    public byte[] receiveDecrypt() throws IOException, GeneralSecurityException {
        byte[] message = receive();
        if(message.length%8 == 0){
            return sessionEncrypter.decrypt(message);
        } else {
            int ordinalPartLength = (message.length / 8) * 8;
            ByteBuffer ordinalPart = ByteBuffer.allocate(ordinalPartLength);
            ByteBuffer lastBytes = ByteBuffer.allocate(message.length - ordinalPartLength);
            ordinalPart.put(message, 0, ordinalPartLength);
            lastBytes.put(message, ordinalPartLength, message.length - ordinalPartLength);
            ByteBuffer decryptedMessage = ByteBuffer.allocate(message.length);
            decryptedMessage.put(sessionEncrypter.decrypt(ordinalPart.array())).put(symmetric(lastBytes.array()));
            return decryptedMessage.array();
        }
    }
    public synchronized byte[] receive() throws IOException { 
        logger.debug("Receiving message...");
        if(state!=State.ESTABLISHED) throw new IOException("receive operation while connection is down");
        byte[] buff = new byte[1024];
        int bytesRead = is.read(buff);
        ByteBuffer bb = ByteBuffer.allocate(bytesRead);
        bb.put(buff,0, bytesRead);
        logger.debug("Received a message: {}", byteArrayToString(bb.array()));
        return bb.array();
    }

    public CMPClient(InputStream is, OutputStream os) {
        this.is = new BufferedInputStream(is);
        this.os = new BufferedOutputStream(os);
        logger.debug("Scheduling a keep-aliver...");
        keepAliver = ses.scheduleAtFixedRate(new KeepAliver(), 3, 3, TimeUnit.SECONDS);
    }

    private short crc16(byte[] buff) {
        short nCRC16 = 0;
        for (byte b : buff) {
            short a1 = (short) (b ^ nCRC16);
            short a2 = (short) (a1 << 4);
            short a3 = (short) (a1 ^ a2);
            nCRC16 = (short) ((nCRC16 >> 8) ^ (a3 >> 12) ^ (a3 >> 5) ^ a3);
        }
        return nCRC16;
    }
    public static String byteArrayToString(byte[] bytes){
        StringWriter sw = new StringWriter();
        sw.append(String.format("%02X", bytes[0]));
        for (int i=1; i<bytes.length; i++){
           sw.append(String.format("-%02X", bytes[i]));
        }
        return sw.toString();
    }
    public static byte[] hexStringToByteArray(String s){
        String[] bytes = s.split("-");
        ByteBuffer byteBuff = ByteBuffer.allocate(bytes.length);
        for(String b : bytes){
            byteBuff.put((byte)(Integer.parseInt(b, 16) - 0x100));
        }
        logger.debug("Converted a hex string: {}", byteArrayToString(byteBuff.array()));
        return byteBuff.array();
    }
    private byte[] symmetric(byte []src){
        byte [] dst = new byte[src.length];
        for (int i = 0; i < src.length; i++){
            dst[i] = (byte)(src[i] ^ i ^ sessionKeyArray[i % 8]);
        }
        return dst;
    }
}
