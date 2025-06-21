package etu.ensicaen.server;

import java.io.InputStream;
import java.util.Properties;

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

    public static int port() {
        String val = props.getProperty("server.port");
        return Integer.parseInt(val);
    }
}
