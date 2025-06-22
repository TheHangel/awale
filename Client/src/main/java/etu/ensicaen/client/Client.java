package etu.ensicaen.client;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Leaderboard;

import java.io.*;
import java.net.Socket;

public class Client {
    private static Client client;
    private final Socket             socket;
    private final ObjectInputStream  in;
    private final ObjectOutputStream out;

    private Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out    = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public static Client get(String host, int port) throws IOException {
        if(client == null) {
            client = new Client(host, port);
        }
        return client;
    }

    public static Client get() {
        return client;
    }

    public String sendCommand(String cmd) throws IOException, ClassNotFoundException {
        out.writeObject(cmd);
        Object resp = in.readObject();
        return (resp instanceof String) ? (String) resp : null;
    }

    public String host(String username) throws IOException, ClassNotFoundException {
        return this.sendCommand("HOST:" + username);
    }

    public String join(String id, String username) throws IOException, ClassNotFoundException {
        return this.sendCommand("JOIN:" + id + ":" + username);
    }

    public Game play() throws IOException, ClassNotFoundException {
        out.writeObject("PLAY");
        return (Game) in.readObject();
    }

    public void select(int index) throws IOException {
        out.writeObject("SELECT:" + index);
        out.flush();
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public void forfeit() throws IOException {
        out.writeObject("FORFEIT");
        out.flush();
    }

    public void respondForfeit() throws IOException {
        out.writeObject("RESPOND_FORFEIT");
        out.flush();
    }

    public Leaderboard leaderboard() throws IOException, ClassNotFoundException {
        out.writeObject("LEADERBOARD");
        out.flush();
        return (Leaderboard) in.readObject();
    }

    public void close() throws IOException {
        socket.close();
    }
}