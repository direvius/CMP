/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author direvius
 */
public class CMPClient {

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
    private static final SecretKey terminalKey = new SecretKeySpec(TERMINAL_KEY, "DES");
    private static final SecretKey transportKey = new SecretKeySpec(TRANSPORT_KEY, "DES");
    private Encrypter sessionEncrypter;
    private enum State {

        DOWN, ESTABLISHED
    }
    private State state = State.DOWN;
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> keepAliver;
    private SecretKey sessionKey;
    
    private class KeepAliver implements Runnable {
        public void run() {
            try {
                keepAlive();
            } catch (IOException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized void keepAlive() throws IOException {
        if(state == State.ESTABLISHED){
            os.write(BEL);
            os.flush();
            is.read();
        }
    }

    public synchronized void send(byte[] message) throws IOException {
        if(state!=State.ESTABLISHED) throw new IOException("send operation while connection is down");
        ByteBuffer bb = ByteBuffer.allocate(message.length + 3).putShort((short) message.length).put(message).put(ETX);
        ByteBuffer bb2 = ByteBuffer.allocate(bb.capacity() + 3);
        bb2.put(STX).put(bb.array()).putShort(crc16(bb.array()));
        os.write(bb2.array());
        os.flush();
    }

    public synchronized void close() throws IOException {
        if(state!=State.ESTABLISHED) throw new IOException("close operation while connection is down");
        os.write(EOT);
        os.flush();
        keepAliver.cancel(true);
        is.close();
        os.close();
        state = State.DOWN;
    }

    public synchronized void open() throws IOException {
        if(state==State.ESTABLISHED) return; // already opened
        
        os.write(ENQ);
        os.write(PROTO_VERSION);
        os.flush();
        if(is.read() == ACK){
            try {
                byte[] buff = new byte[ENQ_RESP_LEN];
                is.read(buff);
                
                Encrypter tdesTerminalEncrypter = new Encrypter(TDES_TRANSFORMATION,terminalKey);
                Encrypter tdesTransportEncrypter = new Encrypter(TDES_TRANSFORMATION,transportKey);
                
                byte [] decryptedKey = tdesTerminalEncrypter.decrypt(buff);
                decryptedKey[7] = 0x7;
                byte [] encryptedKey = tdesTransportEncrypter.encrypt(decryptedKey);
                
                sessionKey = new SecretKeySpec(encryptedKey, "DES");
                
                sessionEncrypter = new Encrypter(DES_TRANSFORMATION, sessionKey);
                
                state = State.ESTABLISHED;
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new IOException("Could not establish connection");
        }
    }

    public synchronized byte[] receive() throws IOException { 
        if(state!=State.ESTABLISHED) throw new IOException("receive operation while connection is down");
        byte[] buff = new byte[1024];
        int bytesRead = is.read(buff);
        ByteBuffer bb = ByteBuffer.allocate(bytesRead);
        bb.put(buff,0, bytesRead);
        return bb.array();
    }

    public CMPClient(InputStream is, OutputStream os) {
        this.is = new BufferedInputStream(is);
        this.os = new BufferedOutputStream(os);
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
        for (byte theByte : bytes)
        {
           sw.append(String.format(" 0x%02X", theByte));
        }
        return sw.toString();
    }
    public static byte[] hexStringToByteArray(String s){
        String[] bytes = s.split("-");
        ByteBuffer byteBuff = ByteBuffer.allocate(bytes.length);
        for(String b : bytes){
            byteBuff.put(Byte.parseByte(b, 16));
        }
        return byteBuff.array();
    }
}
