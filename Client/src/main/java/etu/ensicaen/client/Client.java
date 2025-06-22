package etu.ensicaen.client;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Leaderboard;

import java.io.*;
import java.net.Socket;

public class Client {
    private static Client client;
    private final Socket  socket;
    private final ObjectInputStream  in;
    private final ObjectOutputStream out;

    private final Object readLock = new Object();
    private final Object writeLock = new Object();

    private static String lastHost;
    private static int lastPort;

    private Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public static Client get(String host, int port) throws IOException {
        if(client == null) {
            lastHost = host;
            lastPort = port;
            client = new Client(host, port);
        }
        return client;
    }

    public static Client get() {
        return client;
    }

    public static void reconnect() throws IOException {
        if (client != null) {
            client.close();
        }
        client = new Client(lastHost, lastPort);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public String sendCommand(String cmd) throws IOException, ClassNotFoundException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject(cmd);
            out.flush();
        }
        synchronized (readLock) {
            Object resp = in.readObject();
            return (resp instanceof String) ? (String) resp : null;
        }
    }

    public String host() throws IOException, ClassNotFoundException {
        return this.sendCommand("HOST");
    }

    public String join(String id) throws IOException, ClassNotFoundException {
        return this.sendCommand("JOIN:" + id);
    }

    public Game play() throws IOException, ClassNotFoundException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("PLAY");
            out.flush();
        }
        synchronized (readLock) {
            return (Game) in.readObject();
        }
    }

    public void select(int index) throws IOException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("SELECT:" + index);
            out.flush();
        }
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        synchronized (readLock) {
            return in.readObject();
        }
    }

    public void forfeit() throws IOException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("FORFEIT");
            out.flush();
        }
    }

    public void respondForfeit() throws IOException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("RESPOND_FORFEIT");
            out.flush();
        }
    }

    public Leaderboard leaderboard() throws IOException, ClassNotFoundException {
        synchronized (writeLock) {
            out.writeObject("LEADERBOARD");
            out.flush();
        }
        synchronized (readLock) {
            Object obj = in.readObject();
            if (obj instanceof Leaderboard lb) {
                return lb;
            } else {
                throw new IOException("Expected Leaderboard, got: " + obj.getClass());
            }
        }
    }

    public void close() throws IOException {
        try {
            in.close();
        } catch (Exception ignored) {}
        try {
            out.close();
        } catch (Exception ignored) {}
        socket.close();
    }
}