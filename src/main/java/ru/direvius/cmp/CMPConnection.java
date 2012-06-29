/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.IOException;

/**
 *
 * @author direvius
 */
public interface CMPConnection{
    public void open() throws IOException;
    public void close() throws IOException;
    public void send(byte[] message) throws IOException;
    public byte[] receive() throws IOException;
}
