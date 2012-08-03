package ru.direvius.cmp;

import java.nio.ByteBuffer;

class TLV {

    final byte tag;
    final byte[] value;
    final byte[] lengthField;
    public final int length;

    TLV(byte tag, byte[] value) {
        this.tag = tag;
        this.value = value;
        if((value.length & 0x80) > 0){
            this.lengthField = ByteBuffer.allocate(2).put((byte) 0x81).put((byte) value.length).array();
        }else{
            this.lengthField = ByteBuffer.allocate(1).put((byte) value.length).array();
        }
        this.length = value.length + lengthField.length + 1;
    }

    public byte[] asByteArray() {
        ByteBuffer bb = ByteBuffer.allocate(length);
        bb.put(tag).put(lengthField).put(value);
        return bb.array();
    }
}