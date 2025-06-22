package etu.ensicaen.client;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class to load server host and port from properties file.
 */
public class Config {
    /**
     * Properties object to hold the configuration values.
     */
    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("client.properties")) {
            if (in == null) {
                throw new RuntimeException("cannot find client.properties");
            }
            props.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Returns the server host from the properties file.
     *
     * @return the server host
     */
    public static String host() {
        return props.getProperty("server.host");
    }

    /**
     * Returns the server port from the properties file.
     *
     * @return the server port as an integer
     */
    public static int port() {
        String val = props.getProperty("server.port");
        return Integer.parseInt(val);
    }
}
