package ru.direvius.cmp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    private static byte[] keyBytes = {0x20, 0x21, 0x22, 0x23, 0x24, 0x26, 0x27, 0x28};
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket("10.0.3.70", 688);
            CMPClient cc = new CMPClient(s.getInputStream(), s.getOutputStream());
            cc.open();
            cc.sendEncrypt(CMPClient.hexStringToByteArray("01-01-01-02-04-00-87-82-F8-03-08-09-00-00-00-10-40-92-59-04-04-4F-C8-52-CE"));
            cc.receiveDecrypt();
            Thread.sleep(5000);
            cc.close();
            System.exit(0);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
