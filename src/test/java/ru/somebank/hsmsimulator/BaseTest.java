package ru.somebank.hsmsimulator;

import org.jpos.security.EncryptedPIN;
import org.jpos.security.SMAdapter;
import org.jpos.security.SecureDESKey;
import org.jpos.security.jceadapter.JCESecurityModule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

   // private String host = "s-msk-v-onl-app01";
    //private String host = "10.242.250.163";
    private String host = "localhost";
    private int port = 9999;

    @Test
    public void testMultiThreading() throws Exception{

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for(int i = 0; i < 100000; i++){
            MultiThreadingClient multiThreadingClient = new MultiThreadingClient(host, port);
            threadPool.execute(multiThreadingClient);

            //testHSM();
            //Thread.sleep(1);
        }

    }

    @Test
    public void testHSM() throws Exception{

        Socket socket = new Socket(host, port);
        socket.setSoTimeout(10000);


        // NC command
        byte[] command = new byte[1024];
        command[0] = 0; // starting byte
        command[1] = 6; // message length (number of bytes following this one)
        command[2] = 1; // first sequence byte
        command[3] = 1; // second sequence byte
        command[4] = 1; // third sequence byte
        command[5] = 1; // fourth sequence byte
        command[6] = 78; // N
        command[7] = 67; // C
        /*command[6] = 67;
        command[7] = 65;
        command[8] = 55;
        command[9] = 56;
        command[10] = 57;
        command[11] = 58;*/

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

            out.write(command); // todo - need decodeHex from hex?
            out.flush();
            log.debug("write bytes ...");

            byte[] responseBuffer = new byte[1024];
            //int charsRead = 0;
           int charsRead = in.read(responseBuffer);
           log.info("Chars read: " + charsRead);
                String response = new String(responseBuffer).substring(6, charsRead);
                //String response = ISOUtil.hexString(responseBuffer);
               log.info("HSM response = {}", response);
               //return;

              //Assert.assertEquals("ND00", response);


        } catch (Exception e) {
            log.error("HSM error", e);
        }

    }


    @Test
    public void translateTpkToZpk() throws Exception{

        String tpkRequest = "0031CAU4686C6BE2F59C7631BA1B4DBFC22B68CU0BB1C9A851B0098415E5EE585CC7CDC61283D1D1E9B78F83F40101620600000060";
        String data = tpkRequest.substring(6);

        System.out.println("TPK Request Data: " + data);


        //Get TPK from Request
        if(data.startsWith("U")){
            String tpk = data.substring(0, 33);
            System.out.println("TPK: " + tpk);
            data = data.substring(33);
        }


        //Get Destination key
        if(data.startsWith("U")){
            String destinationKey = data.substring(0, 33);
            System.out.println("Destination key: " + destinationKey);
            data = data.substring(33);

        }

        //Get Maximum PIN length
        String maxPINLength = data.substring(0, 2);
        System.out.println("Maximum PIN length: " + maxPINLength);
        data = data.substring(2);

        //Get Source PIN Block
        String sourcePINBlock = data.substring(0, 16);
        System.out.println("Source PIN Block: " + sourcePINBlock);
        data = data.substring(16);

        //Get Source PIN Block Format
        String sourcePINBlockFormat = data.substring(0, 2);
        System.out.println("Source PIN Block Format: " + sourcePINBlockFormat);
        data = data.substring(2);

        //Get Destination PIN Block Format
        String destinationPINBlockFormat = data.substring(0, 2);
        System.out.println("Destination PIN Block Format: " + destinationPINBlockFormat);
        data = data.substring(2);

        //Get Account Number
        String accountNumber = data.substring(0, 12);
        System.out.println("Account Number: " + accountNumber);
        data = data.substring(12);


        //File lmk = new File("secret.lmk");
        URL lmkFile = getClass().getClassLoader().getResource("secret.lmk");
        JCESecurityModule jcesecmod = new JCESecurityModule(lmkFile.getPath());

       // String tpk = tpkRequest.substring()

       SecureDESKey tpk = new SecureDESKey(SMAdapter.LENGTH_DES3_2KEY
                , SMAdapter.TYPE_TPK+":0U","E9F05D2F2DB8A8579CA3E806B35E336F", "6FB1C8");

        SecureDESKey zpk = new SecureDESKey(SMAdapter.LENGTH_DES3_2KEY
                ,SMAdapter.TYPE_ZPK+":0U","34E2FC8EAD7CD07BFA2B7ED5FE4D8212", "6FB1C8");

        EncryptedPIN pinUnderTpk = new EncryptedPIN(sourcePINBlock, SMAdapter.FORMAT01, accountNumber);
        //EncryptedPIN pinUnderZpk = jcesecmod.translatePINImpl(pinUnderTpk, tpk, zpk, SMAdapter.FORMAT01);
        String decryptedPIN = jcesecmod.decryptPINImpl(pinUnderTpk);
        System.out.println("Decrypted PIN: " + decryptedPIN);

        //EncryptedPIN encPIN = jcesecmod.encryptPIN("1234", "123456789012");

        //System.out.println("Encrypted PIN: " + ISOUtil.byte2hex(encPIN.getPINBlock()));

        //System.out.println("Translated PIN: " + ISOUtil.byte2hex(pinUnderZpk.getPINBlock()));
    }



}
