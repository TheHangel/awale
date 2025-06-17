package etu.ensicaen.server;

import etu.ensicaen.shared.models.Player;
import etu.ensicaen.shared.models.PlayerScore;

import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class Session {
    private final String id;
    private final Socket hostSocket;
    private       Socket guestSocket;

    private ArrayList<Player>      players;
    private ArrayList<PlayerScore> scores;

    public Session(Socket hostSocket) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.players = new ArrayList<>();
        this.scores  = new ArrayList<>();

        Player hostPlayer = new Player("name player 1"); // @todo send custom username
        // add the host (who created the session) to the session
        this.players.add(hostPlayer);
        this.scores.add(new PlayerScore(hostPlayer));
        this.hostSocket = hostSocket;
    }

    public String getId() {
        return id;
    }

    public synchronized boolean addGuest(Socket socket) {
        if (guestSocket == null) {
            guestSocket = socket;
            Player hostPlayer = new Player("name player 2"); // @todo send custom username
            this.players.add(hostPlayer);
            this.scores.add(new PlayerScore(hostPlayer));
            return true;
        }
        return false;
    }

    public boolean isFull() {
        return hostSocket != null && guestSocket != null;
    }

    public Socket getOther(Socket s) {
        if (s == hostSocket) return guestSocket;
        if (s == guestSocket) return hostSocket;
        return null;
    }
}