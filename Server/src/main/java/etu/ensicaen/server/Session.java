package etu.ensicaen.server;

import java.net.Socket;
import java.util.UUID;

public class Session {
    private final String id;
    private final Socket hostSocket;
    private Socket guestSocket;

    public Session(Socket hostSocket) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.hostSocket = hostSocket;
    }

    public String getId() {
        return id;
    }

    public synchronized boolean addGuest(Socket socket) {
        if (guestSocket == null) {
            guestSocket = socket;
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