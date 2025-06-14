package etu.ensicaen.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server();

        try {
            server.waiting();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            server.close();
        }
    }
}