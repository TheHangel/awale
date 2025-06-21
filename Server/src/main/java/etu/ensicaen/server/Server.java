package etu.ensicaen.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private static Server server;
    private final ServerSocket serverSocket;

    // link <id session> to <session>
    private final ConcurrentMap<String,Session> sessions = new ConcurrentHashMap<>();
    // link client <socket>, to server <session>
    private final ConcurrentMap<Socket, Session> socketSessions = new ConcurrentHashMap<>();

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
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream())
        ) {
            out.flush();
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof String) {
                    String line = ((String) obj).trim();

                    if ("HOST".equalsIgnoreCase(line)) {
                        // session creation
                        Session s = new Session(socket);
                        sessions.put(s.getId(), s);
                        socketSessions.put(socket, s);
                        out.writeObject("SESSION_ID:" + s.getId());
                        out.flush();
                        s.setHostStream(out);
                    }
                    else if (line.toUpperCase().startsWith("JOIN:")) {
                        // join session
                        String id = line.substring(5).trim();
                        Session s = sessions.get(id);
                        if (s != null && s.addGuest(socket)) {
                            socketSessions.put(socket, s);
                            out.writeObject("JOINED:" + id);
                        }
                        else {
                            out.writeObject("ERROR:Invalid session or full");
                        }
                        out.flush();
                        assert s != null;
                        s.setGuestStream(out);
                    }
                    else if ("PLAY".equalsIgnoreCase(line)) {
                        // PLAY command handled here
                        Session session = socketSessions.get(socket);
                        if (session != null) {
                            out.flush();
                            if (session.isFull()) {
                                session.initGame(); //TODO to test
                                out.writeObject(session.getCurrentGame());
                                out.flush();
                            }
                        }
                        else {
                            out.writeObject("ERROR:Not in session");
                            out.flush();
                        }
                    }
                    else if(line.toUpperCase().startsWith("SELECT:")) {
                        Session session = socketSessions.get(socket);
                        if (session != null) {
                            String indexStr = line.substring(7).trim();
                            int move = Integer.parseInt(indexStr);

                            session.handlePlayerInput(/*player or player index of the session*/ ,move);
                            session.broadcastGame();
                        }
                        else {
                            out.writeObject("ERROR:Not in session");
                            out.flush();
                        }
                    }
                    else {
                        // unknown command
                        out.writeObject("ERROR:Unknown command");
                        out.flush();
                    }
                }
            }
        }
        catch (EOFException eof) {
            // socket closed on client side, need to close it properly
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}