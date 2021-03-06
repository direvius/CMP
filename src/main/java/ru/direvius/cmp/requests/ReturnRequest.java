/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp.requests;

import java.util.Date;
import ru.direvius.cmp.TLVChainBuilder;

/**
 *
 * @author direvius
 */
public class ReturnRequest implements Request {

    private final TLVChainBuilder tcb = new TLVChainBuilder();
    private final Date dt;
    private final Request kkmResponse;

    public ReturnRequest(Date dt, Request kkmResponse) {
        this.dt = dt;
        this.kkmResponse = kkmResponse;
    }

    public byte[] asByteArray() {
        return tcb.putByte((byte) 0x01, (byte) 0x05).putByteArray((byte) 0x02, kkmResponse.asByteArray()).putDate((byte) 0x03, dt).asByteArray();
    }
}
