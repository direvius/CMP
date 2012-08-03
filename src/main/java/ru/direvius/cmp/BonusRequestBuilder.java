/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.util.Date;

/**
 *
 * @author direvius
 */
public class BonusRequestBuilder implements RequestBuilder {

    private final TLVChainBuilder tcb = new TLVChainBuilder();

    public byte[] build() {
        return tcb.asByteArray();
    }
    
    public BonusRequestBuilder(Date dt){
        tcb.putByte((byte) 0x01, (byte) 0x02).putByteArray((byte) 0x02, new CheckModifyKKMBuilder().build()).putDate((byte) 0x04, dt);
    }
}
