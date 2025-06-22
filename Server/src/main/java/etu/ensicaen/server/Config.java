package etu.ensicaen.server;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to load and provide access to server configuration properties.
 */
public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("server.properties")) {
            if (in == null) {
                throw new RuntimeException("cannot find server.properties");
            }
            props.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Returns the configured port number for the server.
     *
     * @return the port number as an integer
     * @throws NumberFormatException if the port value is not a valid integer
     */
    public static int port() {
        String val = props.getProperty("server.port");
        return Integer.parseInt(val);
    }
}
