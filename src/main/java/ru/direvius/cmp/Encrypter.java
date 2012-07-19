/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;

/**
 *
 * @author direvius
 */
public class Encrypter {
    Cipher ecipher;
    Cipher dcipher;

    Encrypter(String transformation, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        ecipher = Cipher.getInstance(transformation);
        dcipher = Cipher.getInstance(transformation);
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public byte[] encrypt(byte[] message) throws IllegalBlockSizeException, BadPaddingException {
        byte[] enc = ecipher.doFinal(message);
        return enc;
    }

    public byte[] decrypt(byte[] message) throws IllegalBlockSizeException, BadPaddingException {
        byte[] dec = dcipher.doFinal(message);
        return dec;
    }
}