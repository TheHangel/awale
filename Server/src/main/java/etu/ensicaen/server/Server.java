package etu.ensicaen.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private static Server server;
    private ServerSocket serverSocket;
    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    private Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
    }

    public static Server get() throws IOException {
        if(server == null) {
            server = new Server(12345);
        }
        return server;
    }

    public void start() throws IOException {
        while (true) {
            // attente message client
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private void handleClient(Socket socket) {
        try (
                ObjectInputStream  objIn  = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream())
        ) {
            Object cmd = objIn.readObject();
            if (cmd instanceof String) {
                String line = ((String) cmd).trim();
                if ("HOST".equalsIgnoreCase(line)) {
                    Session session = new Session(socket);
                    sessions.put(session.getId(), session);
                    objOut.writeObject("SESSION_ID:" + session.getId());
                    System.out.println("Created session " + session.getId());
                    relay(session, socket, objIn, objOut);
                }
                else if (line.toUpperCase().startsWith("JOIN:")) {
                    String id = line.substring(5).trim();
                    Session session = sessions.get(id);
                    if (session != null && session.addGuest(socket)) {
                        objOut.writeObject("JOINED:" + id);
                        System.out.println("Client joined session " + id);
                        relay(session, socket, objIn, objOut);
                    }
                    else {
                        objOut.writeObject("ERROR:Invalid session or full");
                    }
                }
                else {
                    objOut.writeObject("ERROR:Unknown command");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void relay(Session session, Socket socket,
                       ObjectInputStream in, ObjectOutputStream out) {
        try {
            Object obj;
            while ((obj = in.readObject()) != null) {
                Socket other = session.getOther(socket);
                if (other != null) {
                    ObjectOutputStream otherOut =
                            new ObjectOutputStream(other.getOutputStream());
                    otherOut.writeObject(obj);
                }
            }
        }
        catch (IOException | ClassNotFoundException ignored) {
            // ignore
        }
        System.out.println("Disconnected from session " + session.getId());
    }
}