package ru.somebank.hsmsimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.somebank.hsmsimulator.handlers.HSMRequestHandler;
import ru.somebank.hsmsimulator.utils.Config;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        Config cfg = Config.getInstance();

        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(cfg.serverPort()));
        ExecutorService threadPool = Executors.newFixedThreadPool(Integer.parseInt(cfg.threadPoolCount()));

        log.info("HSM Mock Server started on port {}", cfg.serverPort());

        while(true){

            HSMRequestHandler clientHandler = new HSMRequestHandler(serverSocket.accept());
            threadPool.execute(clientHandler);
        }
    }
}
