package etu.ensicaen.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket             socket;
    private ObjectInputStream  in;
    private ObjectOutputStream out;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out    = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public String sendCommand(String cmd) throws IOException, ClassNotFoundException {
        out.writeObject(cmd);
        Object resp = in.readObject();
        return (resp instanceof String) ? (String) resp : null;
    }

    public String host() throws IOException, ClassNotFoundException {
        return this.sendCommand("HOST");
    }

    public String join(String id) throws IOException, ClassNotFoundException {
        return this.sendCommand("JOIN:" + id);
    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public void close() throws IOException {
        socket.close();
    }
}