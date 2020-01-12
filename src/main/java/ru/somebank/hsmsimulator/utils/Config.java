package ru.somebank.hsmsimulator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс - читатель файла конфигурации app.properties (singleton)
 */
public class Config {

    private final String file = "app.properties";
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private static Config instance;
    private Properties prop;

    /**
     * Порт симулятора
     * @return порт симулятора
     */
    public String serverPort(){
        return prop.getProperty("serverPort");
    }

    /**
     * Количество потоков в пуле
     * @return
     */
    public String threadPoolCount(){
        return prop.getProperty("threadPoolCount");
    }


    public static Config getInstance(){

        if(instance != null){
            return instance;
        }

        instance = new Config();
        return instance;

    }

    private Config(){

        try {
            prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
            log.info("Config file {} is loaded successfully", file);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file " + file + " not found in the classpath");
            }

        } catch (Exception e) {
            log.error("Exception: " + e);
        }

    }
}
