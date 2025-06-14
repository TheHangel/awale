package etu.ensicaen.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ServerSocket serverSocket;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(2009);
    }

    public void waiting() throws IOException {
        this.serverSocket.accept();
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

    public void sendToClient() throws IOException {
        Socket socket = new Socket(2009);
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
    }
}
