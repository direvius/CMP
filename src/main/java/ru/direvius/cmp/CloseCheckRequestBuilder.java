package ru.direvius.cmp;

/**
 *
 * @author direvius
 */
public class CloseCheckRequestBuilder implements RequestBuilder {

    private final TLVChainBuilder tcb = new TLVChainBuilder();

    public byte[] build() {
        return tcb.asByteArray();
    }

    public CloseCheckRequestBuilder() {
        tcb.putByte((byte) 0x01, (byte) 0x03).putByteArray((byte) 0x02, new CloseCheckKKMBuilder().build());
    }
}
