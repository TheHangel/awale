package etu.ensicaen.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Server(12345).start();
    }
}