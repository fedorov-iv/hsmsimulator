package ru.somebank.hsmsimulator.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;


public class HSMRequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HSMRequestHandler.class);

    private Socket clientSocket;

    public HSMRequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {

        log.info("Request registered on address {} port {}", clientSocket.getInetAddress(), clientSocket.getLocalPort());

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))){

            byte[] requestBuffer = new byte[1024];
            int charsRead;
            while ((charsRead = in.read(requestBuffer)) != -1) {

                log.info("HSM request = {}", new String(requestBuffer).substring(0, charsRead));

                byte[] responseBuffer = new byte[10];
                responseBuffer[0] = requestBuffer[0]; // starting byte (always 0)
                responseBuffer[1] = (byte)(responseBuffer.length - 2); //response message length (number of bytes following this one)
                responseBuffer[2] = requestBuffer[2];
                responseBuffer[3] = requestBuffer[3];
                responseBuffer[4] = requestBuffer[4];
                responseBuffer[5] = requestBuffer[5];
                responseBuffer[6] = requestBuffer[6];
                responseBuffer[7] = (byte)(requestBuffer[7] + 1); // response code second byte + 1 (A0->A1, NC->ND and so on)
                responseBuffer[8] = '0'; // 0 error code first byte
                responseBuffer[9] = '0'; // 0 error code second byte

                log.info("HSM response = {}", new String(responseBuffer));

                out.write(responseBuffer);
                out.flush();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
