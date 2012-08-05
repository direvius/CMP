/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.direvius.cmp;

/**
 *
 * @author direvius
 */
public class PartlyReturnKKMBuilder {
    private final byte [] resp = Util.hexStringToByteArray("01-01-0A-03-04-00-01-81-00-09-0A-10-88-80-88-80-00-14-27-42-18-08-36-01-1F-01-02-47-32-02-04-00-00-22-60-03-04-00-00-2B-C0-04-04-00-01-81-00-05-04-80-2D-39-32-06-01-00-02-01-00-03-04-00-01-81-00-06-0A-10-88-80-88-80-00-14-27-42-18");
    private final byte [] retFull = Util.hexStringToByteArray("01-01-0A-09-0A-10-88-80-88-80-00-14-27-42-21");
    public byte[] build() {
        return retFull;
    }
}
