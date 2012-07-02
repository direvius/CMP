/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Queue;
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
    
    private final Queue<byte[]> inbox = new ArrayBlockingQueue<byte[]>(100, true);
    
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
    //private final Future<?> listener;
    private final ScheduledFuture<?> keepAliver;

    private class CMPListener implements Runnable {

        public void run() {
            try {
                for (int r = is.read(); r > 0; r = is.read()) {
                    switch (r) {
                        case ENQ:
                            if (state == State.DOWN) {
                                state = State.ESTABLISHED;
                                os.write(ACK);
                            } else {
                                throw new IllegalStateException("ENQ while in state " + state);
                            }
                            break;
                        case BEL:
                            if (state != State.DOWN) {
                                os.write(BEL);
                            } else {
                                throw new IllegalStateException("BEL while connection is DOWN");
                            }
                            break;
                        case STX:
                            synchronized(os){
                                int length = is.read()<<8 + is.read();
                                byte[] buff = new byte[length];
                                is.read(buff);
                                if(is.read() == ETX){
                                    int crc16 = is.read()<<8 + is.read();
                                    if(crc16 == crc16(buff)){
                                        inbox.add(buff);
                                        os.write(ACK);
                                    } else{
                                        os.write(NAK);
                                    }
                                }
                            }
                            break;
                        case ACK:
                        case NAK:
                            // do nothing
                            break;
                        case EOT:
                            keepAliver.cancel(true);
                            is.close();
                            os.close();
                            return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private class KeepAliver implements Runnable {

        public void run() {
            synchronized(os){
                try {
                    os.write(BEL);
                    System.out.println(is.read());
                } catch (IOException ex) {
                    Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    public void send(byte[] message) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(message.length + 3)
                .putShort((short) message.length)
                .put(message)
                .put(ETX);
        ByteBuffer bb2 = ByteBuffer.allocate(bb.capacity()+3);
        bb2.put(STX)
                .put(bb.array())
                .putShort(crc16(bb.array()));
        os.write(bb2.array());
    }
    public void close() throws IOException {
        synchronized(os){
            os.write(EOT);
            //listener.cancel(true);
            keepAliver.cancel(true);
            is.close();
            os.close();
        }
    }
    public void open() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(3);
        bb.put(ENQ).put((byte)2).put((byte)0);
        os.write(bb.array());
    }
    public byte[] waitMessage(){
        return inbox.remove();
    }
    public byte[] read() throws IOException{
        synchronized(os){
            int nAvailable = is.available();
            if(nAvailable>0){
                byte[] buff = new byte[nAvailable];
                is.read(buff);
                return buff;
            }else return new byte[0];
        }
    }
    public CMPClient(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        //listener = es.submit(new CMPListener());
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
