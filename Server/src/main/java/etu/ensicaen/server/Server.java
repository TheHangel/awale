package etu.ensicaen.server;

import etu.ensicaen.shared.models.Leaderboard;
import etu.ensicaen.shared.models.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The main server class responsible for managing incoming client connections,
 * creating and managing game sessions, and routing commands between clients and the server logic.
 */
public class Server {
    private static Server server;
    private final ServerSocket serverSocket;
    private static final Leaderboard leaderboard = Leaderboard.load();

    /** Maps session ID to corresponding {@link Session} object. */
    private final ConcurrentMap<String,Session> sessions = new ConcurrentHashMap<>();

    /** Maps each client's {@link Socket} to its {@link Session}. */
    private final ConcurrentMap<Socket, Session> socketSessions = new ConcurrentHashMap<>();

    /**
     * Constructs the server and binds it to the specified port.
     *
     * @param port the port to bind the server socket to
     * @throws IOException if an I/O error occurs when opening the socket
     */
    private Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
    }

    /**
     * Retrieves the static leaderboard instance.
     *
     * @return the {@link Leaderboard}
     */
    public static Leaderboard getLeaderboard() {
        return leaderboard;
    }

    /**
     * Returns the singleton instance of the server, creating it if needed.
     *
     * @return the singleton {@link Server} instance
     * @throws IOException if the server cannot be started
     */
    public static Server get() throws IOException {
        if(server == null) {
            server = new Server(Config.port());
        }
        return server;
    }

    /**
     * Starts listening for incoming client connections and spawns a new thread
     * to handle each client.
     *
     * @throws IOException if the server socket fails
     */
    public void start() throws IOException {
        while (true) {
            // attente message client
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    /**
     * Handles all communication with a connected client.
     * Processes commands such as HOST, JOIN, PLAY, SELECT, FORFEIT, and LEAVE.
     *
     * @param socket the client socket
     */
    private void handleClient(Socket socket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream())
        ) {
            out.reset();
            out.flush();
            while (true) {
                Object obj;
                try {
                    obj = in.readObject();
                } catch (EOFException | SocketException eof) {
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                    break;
                }
                if (obj instanceof String) {
                    String line = ((String) obj).trim();

                    if (line.toUpperCase().startsWith("HOST:")) {
                        String username = line.substring(5);
                        // session creation
                        Session s = new Session(socket, username);
                        sessions.put(s.getId(), s);
                        socketSessions.put(socket, s);
                        out.writeObject("SESSION_ID:" + s.getId());
                        out.flush();
                        s.setHostStream(out);
                    }
                    else if (line.toUpperCase().startsWith("JOIN:")) {
                        // join session
                        String id = line.substring(5, 13).trim();
                        String username = line.substring(14).trim();
                        Session s = sessions.get(id);
                        if (s != null && s.addGuest(socket, username)) {
                            socketSessions.put(socket, s);
                            out.writeObject("JOINED:" + id);
                            out.flush();
                            s.setGuestStream(out);
                        } else {
                            out.writeObject("ERROR:Invalid session or full");
                            out.flush();
                        }
                    }
                    else if ("PLAY".equalsIgnoreCase(line)) {
                        // PLAY command handled here
                        Session session = socketSessions.get(socket);
                        if (session != null) {
                            out.flush();
                            if (session.isFull()) {
                                session.initGame();
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

                            session.handlePlayerInput(socket ,move);
                            session.broadcastGame();
                        }
                        else {
                            out.writeObject("ERROR:Not in session");
                            out.flush();
                        }
                    }
                    else if ("FORFEIT".equalsIgnoreCase(line)) {
                        Session session = socketSessions.get(socket);
                        if (session != null) {
                            // send give up to other player
                            session.broadcastTo(session.getOtherOutputStream(socket), "ASK_FORFEIT");
                        }
                        else {
                            out.writeObject("ERROR:Not in session");
                            out.flush();
                        }
                    }
                    else if ("RESPOND_FORFEIT".equalsIgnoreCase(line)) {
                        Session session = socketSessions.get(socket);
                        if (session != null) {
                            // send give up to other player
                            session.getCurrentGame().handleForfeit();
                            session.broadcastGame();
                            session.checkGameStatus();
                        }
                        else {
                            out.writeObject("ERROR:Not in session");
                            out.flush();
                        }
                    }
                    else if ("LEADERBOARD".equalsIgnoreCase(line)) {
                        Leaderboard lb = Leaderboard.load();
                        out.reset();
                        out.writeObject(lb);
                        out.flush();
                    } else if ("LEAVE".equalsIgnoreCase(line)) {
                        Session session = socketSessions.get(socket);
                        if (session != null) {
                            ObjectOutputStream otherOut = session.getOtherOutputStream(socket);
                            if (otherOut != null) {
                                session.broadcastTo(otherOut, Messages.LEAVE_MESSAGE);
                            }
                            sessions.remove(session.getId());
                            socketSessions.remove(socket);
                        }
                        break;
                    }
                    else {
                        // unknown command
                        out.writeObject("ERROR:Unknown command");
                        out.flush();
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}