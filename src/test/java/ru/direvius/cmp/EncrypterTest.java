/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;

/**
 *
 * @author direvius
 */
public class EncrypterTest extends TestCase {
    private static final String TDES_TRANSFORMATION = "DESede/ECB/NoPadding";
    private static final String DES_TRANSFORMATION = "DES/ECB/NoPadding";
    private static final byte[] TERMINAL_KEY = Util.hexStringToByteArray("C3-5F-ED-30-8F-9C-34-F4-E7-E4-83-3C-67-67-13-EC-24-08-0C-3E-34-C8-F7-BD");
    private static final byte[] TRANSPORT_KEY = Util.hexStringToByteArray("EF-B1-CF-18-5A-9E-DB-B7-0E-C7-F8-0A-E7-28-9A-EA-24-45-1A-EB-4E-09-E2-FE");
    private static final SecretKey terminalKey = new SecretKeySpec(TERMINAL_KEY, "DESede");
    private static final SecretKey transportKey = new SecretKeySpec(TRANSPORT_KEY, "DESede");
    private static final String SERVER_RESPONSE =  "8E-B1-75-36-F7-4A-8A-2C";
    private static final String DECRYPTED_SERVER_RESPONSE = "01-79-7C-D1-28-15-E2-8B";
    private static final String DECRYPTED_SERVER_RESPONSE_07 = "01-79-7C-D1-28-15-E2-07";
    private static final String SESSION_KEY = "FF-C4-60-0A-AE-63-0B-56";
    public EncrypterTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of encrypt method, of class Encrypter.
     */
    public void testEncrypt() throws Exception {
        System.out.println("encrypt");
        byte[] message = Util.hexStringToByteArray(DECRYPTED_SERVER_RESPONSE_07);
        Encrypter instance = new Encrypter(TDES_TRANSFORMATION,terminalKey);
        byte[] result = instance.encrypt(message);
        System.out.println(Util.byteArrayToString(result));
        assertEquals(SESSION_KEY, Util.byteArrayToString(result));
    }

    /**
     * Test of decrypt method, of class Encrypter.
     */
    public void testDecrypt() throws Exception {
        System.out.println("decrypt");
       byte[] message = Util.hexStringToByteArray(SERVER_RESPONSE);
        Encrypter instance = new Encrypter(TDES_TRANSFORMATION,transportKey);
        byte[] result = instance.decrypt(message);
        System.out.println(Util.byteArrayToString(result));
        assertEquals(DECRYPTED_SERVER_RESPONSE, Util.byteArrayToString(result));
    }
}
