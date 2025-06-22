package etu.ensicaen.server;

import java.io.IOException;

/**
 * Entry point for the server application.
 */
public class Main {
    /**
     * Main method that launches the server.
     *
     * @param args command-line arguments (not used)
     * @throws IOException if the server fails to start due to network or configuration issues
     */
    public static void main(String[] args) throws IOException {
        Server.get().start();
    }
}