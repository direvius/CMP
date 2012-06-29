/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
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
    
    private enum State{DOWN, ESTABLISHED, TRANSMISSION}
    private State state = State.DOWN;
    
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    
    private final Future<?> listener;
    
    private class CMPListener implements Runnable{

        public void run() {
            try {
                for(int r = is.read(); r>0; r = is.read()){
                    switch(r){
                        case ENQ:
                            if(state == State.DOWN){
                                state = State.ESTABLISHED;
                                os.write(ACK);
                            }else{
                                throw new IllegalStateException("ENQ while in state "+state);
                            }
                            break;
                        case BEL:
                            if(state != State.DOWN){
                                os.write(BEL);
                            }else {
                                throw new IllegalStateException("BEL while connection is DOWN");
                            }
                            break;
                        case STX:
                            //TODO: receive message
                            break;
                        case EOT:
                            //TODO: close connection
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CMPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public CMPClient(InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
        listener = es.submit(new CMPListener());
    }
}
