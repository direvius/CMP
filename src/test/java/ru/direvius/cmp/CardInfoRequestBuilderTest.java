/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author direvius
 */
public class CardInfoRequestBuilderTest extends TestCase {
    
    public CardInfoRequestBuilderTest(String testName) {
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
     * Test of build method, of class CardInfoRequestBuilder.
     */
    public void testBuild() {
        System.out.println("build");
        CardInfoRequestBuilder instance = new CardInfoRequestBuilder((byte)1, 0x8782F8, 0x0900000010409259L, new Date());
        System.out.println(Util.byteArrayToString(instance.build()));
    }

}
