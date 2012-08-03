/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

/**
 *
 * @author direvius
 */
public class CloseCheckKKMBuilder {
    private final byte [] resp = Util.hexStringToByteArray("01-01-04-02-01-00-05-07-20-12-05-31-08-17-04-03-04-00-02-0D-00-0A-04-00-00-00-00-0B-04-00-00-00-00-0D-04-00-00-00-00-10-04-00-00-00-00-08-7A-01-2B-01-02-47-32-02-04-00-00-2E-E0-03-04-00-00-2B-C0-04-04-00-02-0D-00-05-04-80-2D-39-32-06-01-02-07-04-00-00-00-00-08-04-00-00-1A-40-01-33-01-07-5A-5A-5A-5A-5A-5A-5A-02-04-00-0A-41-00-03-04-00-00-00-0A-04-04-00-00-1A-40-05-07-5A-61-64-61-74-6F-6B-06-01-08-07-04-00-00-00-00-08-04-00-00-00-00-02-01-02-03-04-00-02-27-40-08-07-20-12-05-31-08-17-04-09-04-00-00-1A-40");
    public byte[] build() {
        return resp;
    }
}
