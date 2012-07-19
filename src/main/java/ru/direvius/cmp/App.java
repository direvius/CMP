package ru.direvius.cmp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
            Thread.sleep(5000);
            cc.close();
            System.exit(0);
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
