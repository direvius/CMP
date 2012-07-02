/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author direvius
 */
public class CMPClient {

    private final InputStream is;
    private final OutputStream os;
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;
    private static final byte EOT = 0x04;
    private static final byte ENQ = 0x05;
    private static final byte ACK = 0x06;
    private static final byte BEL = 0x07;
    private static final byte NAK = 0x15;

    private enum State {

        DOWN, ESTABLISHED
    }
    private State state = State.DOWN;
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> keepAliver;

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
        os.write(BEL);
        os.flush();
        System.out.println(is.read());
    }

    public synchronized void send(byte[] message) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(message.length + 3).putShort((short) message.length).put(message).put(ETX);
        ByteBuffer bb2 = ByteBuffer.allocate(bb.capacity() + 3);
        bb2.put(STX).put(bb.array()).putShort(crc16(bb.array()));
        os.write(bb2.array());
        os.flush();
    }

    public synchronized void close() throws IOException {
        os.write(EOT);
        os.flush();
        keepAliver.cancel(true);
        is.close();
        os.close();
    }

    public synchronized void open() throws IOException {
        os.write(ENQ);
        os.write(2);
        os.write(0);
        os.flush();
        System.out.println(read().length);
    }

    public synchronized byte[] read() throws IOException {   
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
}
