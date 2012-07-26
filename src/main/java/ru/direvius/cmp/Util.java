/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author direvius
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    /*
     * long int to BCD algorythm Found here: https://gist.github.com/953548
     */

    public static byte[] DecToBCDArray(long num) {
        int digits = 0;

        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }

        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
        boolean isOdd = digits % 2 != 0;

        byte bcd[] = new byte[byteLen];

        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);

            if (i == digits - 1 && isOdd) {
                bcd[i / 2] = tmp;
            } else if (i % 2 == 0) {
                bcd[i / 2] = tmp;
            } else {
                byte foo = (byte) (tmp << 4);
                bcd[i / 2] |= foo;
            }

            num /= 10;
        }

        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }

        return bcd;
    }

    public static byte[] crc16(byte[] buff) {
        int nCRC16 = 0;
        for (byte b : buff) {
            int dt = (int) b & 0x00FF;
            //logger.debug(String.format("b: %08X", dt));
            int a1 = (dt ^ nCRC16) & 0xFFFF;
            //logger.debug(String.format("a1: %08X", a1));
            int a2 = (a1 << 4) & 0xFFFF;
            //logger.debug(String.format("a2: %08X", a2));
            int a3 = (a1 ^ a2) & 0xFFFF;
            //logger.debug(String.format("a3: %08X", a3));
            nCRC16 = ((nCRC16 >> 8) ^ (a3 >> 12) ^ (a3 >> 5) ^ a3) & 0xFFFF;
            //logger.debug(String.format("crc: %08X", nCRC16));
        }
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put((byte) ((nCRC16 & 0xFF00) >> 8)).put((byte) (nCRC16 & 0xFF));
        if (logger.isDebugEnabled()) {
            logger.debug("CRC for {}: {}", byteArrayToString(buff), byteArrayToString(bb.array()));
        }

        return bb.array();
    }

    public static String byteArrayToString(byte[] bytes) {
        StringWriter sw = new StringWriter();
        sw.append(String.format("%02X", bytes[0]));
        for (int i = 1; i < bytes.length; i++) {
            sw.append(String.format("-%02X", bytes[i]));
        }
        return sw.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        String[] hexBytes = s.split("-");
        ByteBuffer byteBuff = ByteBuffer.allocate(hexBytes.length);
        for (String b : hexBytes) {
            byteBuff.put((byte) (Integer.parseInt(b, 16) - 0x100));
        }
        //logger.debug("Converted a hex string: {}", byteArrayToString(byteBuff.array()));
        return byteBuff.array();
    }
}
