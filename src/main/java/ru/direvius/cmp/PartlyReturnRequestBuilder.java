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
public class PartlyReturnRequestBuilder  implements RequestBuilder {

    private final TLVChainBuilder tcb = new TLVChainBuilder();

    public byte[] build() {
        return tcb.asByteArray();
    }
    public PartlyReturnRequestBuilder(Date dt){
        tcb.putByte((byte) 0x01, (byte) 0x05).putByteArray((byte) 0x02, new PartlyReturnKKMBuilder().build()).putDate((byte) 0x03, dt);
    }
}
