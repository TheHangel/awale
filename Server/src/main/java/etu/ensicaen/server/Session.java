package etu.ensicaen.server;

import etu.ensicaen.shared.Protocol;
import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Player;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Session implements Runnable {
    private final String id;
    private Socket socket1, socket2;
    private Player player1, player2;
    private Game game;

    public Session(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public synchronized void addPlayer(Socket socket, Player player) {
        if (socket1 == null) {
            socket1 = socket;
            player1 = player;
        } else if (socket2 == null) {
            socket2 = socket;
            player2 = player;
        } else {
            throw new IllegalStateException("Session pleine");
        }
    }

    public boolean isFull() {
        return socket1 != null && socket2 != null;
    }

    @Override
    public void run() {
        try {
            Protocol.writeString(socket1, "START:" + player2.getUsername());
            Protocol.writeString(socket2, "START:" + player1.getUsername());

            while (!socket1.isClosed() && !socket2.isClosed()) {
                String msg1 = Protocol.readString(socket1);
                Protocol.writeString(socket2, msg1);

                String msg2 = Protocol.readString(socket2);
                Protocol.writeString(socket1, msg2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSockets();
            System.out.println("Session " + id + " terminée.");
        }
    }

    private void closeSockets() {
        try { if (socket1 != null) socket1.close(); } catch (IOException ignored) {}
        try { if (socket2 != null) socket2.close(); } catch (IOException ignored) {}
    }
}