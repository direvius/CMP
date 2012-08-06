/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp.requests;

import ru.direvius.cmp.TLVChainBuilder;

/**
 *
 * @author direvius
 */
public class CloseCheckRequest implements Request {

    private final TLVChainBuilder tcb = new TLVChainBuilder();
    private final Request kkmResponse;

    public CloseCheckRequest(Request kkmResponse) {
        this.kkmResponse = kkmResponse;
    }

    public byte[] asByteArray() {
        return tcb.putByte((byte) 0x01, (byte) 0x03).putByteArray((byte) 0x02, kkmResponse.asByteArray()).asByteArray();
    }
}
