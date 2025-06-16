package etu.ensicaen.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendMessage(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }

    public String host() throws IOException {
        return this.sendMessage("HOST");
    }

    public String join(String id) throws IOException {
        return this.sendMessage("JOIN" + id);
    }

    public String readMessage() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }
}