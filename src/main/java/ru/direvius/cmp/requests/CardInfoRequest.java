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
public class CardInfoRequest implements Request {
    private final int terminalID;
    private final long cardNumber;
    private final Date dt;
    
     private final TLVChainBuilder tcb = new TLVChainBuilder();
    
    public CardInfoRequest(int terminalID, long cardNumber, Date dt){
        this.terminalID = terminalID;
        this.cardNumber = cardNumber;
        this.dt = dt;
    }
    public byte[] asByteArray() {
        return tcb.putByte((byte) 0x01, (byte) 0x01).putInt((byte) 0x02, terminalID).putBCD((byte) 0x03, cardNumber).putDate((byte) 0x04, dt).asByteArray();
    }
    
}
