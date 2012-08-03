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
public class CardInfoRequestBuilder implements RequestBuilder {

    private final TLVChainBuilder tcb = new TLVChainBuilder();

    public byte[] build() {
        return tcb.asByteArray();
    }

    public CardInfoRequestBuilder(int terminalID, long cardNumber, Date dt) {
        tcb.putByte((byte) 0x01, (byte) 0x01).putInt((byte) 0x02, terminalID).putBCD((byte) 0x03, cardNumber).putDate((byte) 0x04, dt);
    }

    public CardInfoRequestBuilder addTransactionID(int transactionID) {
        tcb.putBCD((byte) 0x05, transactionID);
        return this;
    }
}
