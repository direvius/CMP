/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import junit.framework.TestCase;

/**
 *
 * @author direvius
 */
public class CMPClientTest extends TestCase {
    
    public CMPClientTest(String testName) {
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
     * Test of CRC16 method, of class CMPClient.
     */
    public void testCrc16() {
        System.out.println("crc16");
        String expResult = "2A-40";
        byte[] data = CMPClient.hexStringToByteArray("00-19-A5-C2-51-29-83-31-C8-AC-06-EA-36-E3-5F-A0-51-64-0B-D8-3E-49-8B-46-D2-1C-CD-03");
        String result = CMPClient.byteArrayToString(CMPClient.crc16(data));
        assertEquals(expResult, result);
    }
}
