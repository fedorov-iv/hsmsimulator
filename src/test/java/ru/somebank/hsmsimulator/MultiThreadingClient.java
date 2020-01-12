package ru.somebank.hsmsimulator;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class MultiThreadingClient  implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(MultiThreadingClient.class);
    private Socket socket;

    public MultiThreadingClient(String host, int port){
        try{
            socket = new Socket(host, port);
            socket.setSoTimeout(10000);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }



    public  void run(){

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

             Assert.assertEquals("ND00", response);


        } catch (Exception e) {
            log.error("HSM error", e);
        }


    }
}
