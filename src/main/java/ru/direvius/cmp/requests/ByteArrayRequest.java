/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp.requests;

import ru.direvius.cmp.Util;

/**
 *
 * @author direvius
 */
public class ByteArrayRequest implements Request {
    private final byte[] array;
    public ByteArrayRequest(String hexString){
        array = Util.hexStringToByteArray(hexString);
    }
    public ByteArrayRequest(byte[] array){
        this.array = array;
    }
    public byte[] asByteArray() {
        return array;
    }
    
}
