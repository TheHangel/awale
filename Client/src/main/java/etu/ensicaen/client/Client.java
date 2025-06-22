package etu.ensicaen.client;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Leaderboard;

import java.io.*;
import java.net.Socket;

/**
 * Client class to handle communication with the server.
 */
public class Client {
    /**
     * Singleton instance of the Client.
     */
    private static Client client;

    /**
     * Socket for communication with the server.
     */
    private final Socket  socket;

    /**
     * Input and output streams for the socket.
     */
    private final ObjectInputStream  in;
    private final ObjectOutputStream out;

    /**
     * Locks for synchronizing read and write operations.
     */
    private final Object readLock = new Object();
    private final Object writeLock = new Object();

    /**
     * Last used host and port for reconnection.
     */
    private static String lastHost;
    private static int lastPort;

    /**
     * Private constructor to initialize the socket and streams.
     *
     * @param host The server host.
     * @param port The server port.
     * @throws IOException If an I/O error occurs when creating the socket or streams.
     */
    private Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Static method to get the singleton instance of the Client.
     *
     * @param host The server host.
     * @param port The server port.
     * @return The singleton instance of the Client.
     * @throws IOException If an I/O error occurs when creating the socket or streams.
     */
    public static Client get(String host, int port) throws IOException {
        if(client == null) {
            lastHost = host;
            lastPort = port;
            client = new Client(host, port);
        }
        return client;
    }

    /**
     * Static method to get the singleton instance of the Client.
     *
     * @return The singleton instance of the Client.
     */
    public static Client get() {
        return client;
    }

    /**
     * Static method to reconnect to the server using the last used host and port.
     *
     * @throws IOException If an I/O error occurs when creating the socket or streams.
     */
    public static void reconnect() throws IOException {
        if (client != null) {
            client.close();
        }
        client = new Client(lastHost, lastPort);
    }

    /**
     * Checks if the client is connected to the server.
     *
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Sends a command to the server and waits for a response.
     *
     * @param cmd The command to send.
     * @return The response from the server as a String.
     * @throws IOException If an I/O error occurs when sending or receiving data.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
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

    /**
     * Sends the host command to the server.
     *
     * @param username The username to host the game with.
     * @return The response from the server as an Object.
     * @throws IOException If an I/O error occurs when sending or receiving data.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
    public String host(String username) throws IOException, ClassNotFoundException {
        return this.sendCommand("HOST:" + username);
    }

    /**
     * Sends the join command to the server.
     *
     * @param id The game ID to join.
     * @param username The username to join the game with.
     * @return The response from the server as a String.
     * @throws IOException If an I/O error occurs when sending or receiving data.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
    public String join(String id, String username) throws IOException, ClassNotFoundException {
        return this.sendCommand("JOIN:" + id + ":" + username);
    }

    /**
     * Sends the play command to the server and waits for a Game object in response.
     *
     * @return The Game object received from the server.
     * @throws IOException If an I/O error occurs when sending or receiving data.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
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

    /**
     * Sends the select command to the server with the specified index.
     * When playing the game.
     *
     * @param index The index to select.
     * @throws IOException If an I/O error occurs when sending data.
     */
    public void select(int index) throws IOException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("SELECT:" + index);
            out.flush();
        }
    }

    /**
     * Reads an object from the input stream.
     *
     * @return The object read from the input stream.
     * @throws IOException If an I/O error occurs when reading the object.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        synchronized (readLock) {
            return in.readObject();
        }
    }

    /**
     * Sends a command to the server to indicate that the player want to forfeit the game.
     *
     * @throws IOException If an I/O error occurs when sending data.
     */
    public void forfeit() throws IOException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("FORFEIT");
            out.flush();
        }
    }

    /**
     * Sends a command to the server to respond to a forfeit request.
     * This is used when the player has been asked to forfeit by the opponent.
     *
     * @throws IOException If an I/O error occurs when sending data.
     */
    public void respondForfeit() throws IOException {
        synchronized (writeLock) {
            out.reset();
            out.writeObject("RESPOND_FORFEIT");
            out.flush();
        }
    }

    /**
     * Requests the leaderboard from the server.
     *
     * @return The Leaderboard object received from the server.
     * @throws IOException If an I/O error occurs when sending or receiving data.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
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

    /**
     * Sends a command to the server to leave the current game.
     *
     * @throws IOException If an I/O error occurs when sending data.
     */
    public void leave() throws IOException {
        out.writeObject("LEAVE");
        out.flush();
    }

    /**
     * Closes the client connection and releases resources.
     *
     * @throws IOException If an I/O error occurs when closing the socket or streams.
     */
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