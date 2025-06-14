// package etu.ensicaen.client;
package etu.ensicaen.client;

import etu.ensicaen.shared.Protocol;   // déplace Protocol en "shared" si possible

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Un client simple qui se connecte au serveur,
 * envoie son nom et échange des messages via Protocol.
 */
public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendMessage(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }
}