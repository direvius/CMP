package ru.direvius.cmp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger("Main");

    public static void main(String[] args) {
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            Socket s = new Socket("10.0.3.70", 688);
            CMPClient cc = new CMPClient(s.getInputStream(), s.getOutputStream());
            cc.open();
            cc.sendEncrypt(Util.hexStringToByteArray("01-01-01-02-04-00-87-82-F8-03-08-09-00-00-00-10-40-92-59-04-04-4F-C8-52-CE"));
            cc.receiveDecrypt();
            Thread.sleep(5000);
            cc.close();
        } catch (GeneralSecurityException ex) {
            logger.error("Cryptography problem", ex);
        } catch (InterruptedException ex) {
            logger.error("Girl, interrupted", ex);
        } catch (UnknownHostException ex) {
            logger.error("Host not found while opening a connection", ex);
        } catch (IOException ex) {
            logger.error("Failed to comunicate", ex);
        } finally {
            System.exit(0);
        }
    }
}
