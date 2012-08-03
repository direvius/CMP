// DOES NOT WORK
package ru.direvius.cmp;

/**
 *
 * @author direvius
 */
public class CheckModifyKKMBuilder implements RequestBuilder {

    //private final TLVChainBuilder tcb = new TLVChainBuilder();
    private final byte [] resp = Util.hexStringToByteArray("01-01-06-03-04-00-02-0D-00-04-02-00-04-08-44-01-1C-01-02-47-32-02-04-00-00-2E-E0-03-04-00-00-2B-C0-04-04-00-02-0D-00-05-04-80-2D-39-32-03-04-00-02-0D-00-0A-1E-01-04-00-02-0D-00-02-04-00-00-00-00-03-04-00-00-00-00-04-04-00-00-00-00-05-04-00-00-00-00");
    public byte[] build() {
        return resp;
        //return tcb.asByteArray();
    }

    /*public CheckModifyKKMBuilder(int amount) {
        throw new NotImplementedException();
        // 0x01 TypeID = 0x06, 0x03 Amount b32, (0x08 FiscalCheque OR 0x04 Flags = )
        //tcb.putByte((byte) 0x01, (byte) 0x06).putInt((byte) 0x03, amount);
    }*/
}
