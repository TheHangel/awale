package etu.ensicaen.client;

import java.io.InputStream;
import java.util.Properties;

public class Config {
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

    public static String host() {
        return props.getProperty("server.host");
    }

    public static int port() {
        String val = props.getProperty("server.port");
        return Integer.parseInt(val);
    }
}
