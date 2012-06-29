/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author direvius
 */
class CMPConnectionImpl implements CMPConnection{
    private final InputStream is;
    private final OutputStream os;
    
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;
    private static final byte EOT = 0x04;
    private static final byte ENQ = 0x05;
    private static final byte ACK = 0x06;
    private static final byte BEL = 0x07;
    private static final byte NAK = 0x15;
    
    private enum State{DOWN, UP}
    private State state = State.DOWN;
    
    private synchronized void write(byte[] message) throws IOException{
        os.write(message);
    }
    
    private synchronized int read(byte [] buffer) throws IOException{
        return is.read(buffer);
    }
    
    public CMPConnectionImpl(InputStream is, OutputStream os){
        this.is = is;
        this.os = os;
    }
    public void open() throws IOException {
        state = State.UP;
    }

    public void close() throws IOException {
        state = State.DOWN;
    }

    public void send(byte[] message) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public byte[] receive() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
