package etu.ensicaen.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private ServerSocket serverSocket;
    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
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
                // contenu du message client
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // à envoyer au client
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // lire message du client
            String line = in.readLine();
            if (line == null) return;

            if ("HOST".equalsIgnoreCase(line)) {
                Session session = new Session(socket);
                sessions.put(session.getId(), session);
                out.println("SESSION_ID:" + session.getId());
                System.out.println("Created session " + session.getId());
                relayMessages(session, socket);
            }
            else if (line.startsWith("JOIN:")) {
                String id = line.substring(5);
                Session session = sessions.get(id);
                if (session != null && session.addGuest(socket)) {
                    out.println("JOINED:" + id);
                    System.out.println("Client joined session " + id);
                    relayMessages(session, socket);
                }
                else {
                    out.println("ERROR:Invalid session or full");
                    socket.close();
                }
            }
            else {
                out.println("ERROR:Unknown command");
                socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void relayMessages(Session session, Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String msg;
            while ((msg = in.readLine()) != null) {
                Socket other = session.getOther(socket);
                if (other != null) {
                    PrintWriter otherOut = new PrintWriter(other.getOutputStream(), true);
                    otherOut.println(msg);
                }
            }
        }
        catch (IOException e) {
            // ignore
        }
        finally {
            System.out.println("Client disconnected from session " + session.getId());
        }
    }
}