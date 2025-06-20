package etu.ensicaen.server;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Player;
import etu.ensicaen.shared.models.PlayerScore;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class Session {
    private final String id;
    private final Socket hostSocket;
    private       Socket guestSocket;
    private final Player[] players = new Player[2];

    private Game currentGame;
    private ObjectOutputStream hostOut;
    private ObjectOutputStream guestOut;

    public Session(Socket hostSocket) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.hostSocket = hostSocket;
        this.players[0] = new Player("Player 1");
    }

    public String getId() { return id; }

    public synchronized boolean addGuest(Socket socket) {
        if (guestSocket == null) {
            guestSocket = socket;
            this.players[1] = new Player("Player 2");
            return true;
        }
        return false;
    }

    public synchronized Game getOrCreateGame() {
        if (currentGame == null) {
            currentGame = new Game(players[0], players[1]);
        }
        return currentGame;
    }

    public synchronized void setHostStream(ObjectOutputStream out) {
        this.hostOut = out;
    }

    public synchronized void setGuestStream(ObjectOutputStream out) {
        this.guestOut = out;
    }

    public synchronized void broadcastGame() throws IOException {
        Game g = getOrCreateGame();
        if (hostOut != null) {
            hostOut.reset();
            hostOut.writeUnshared(g);
            hostOut.flush();
        }
        if (guestOut != null) {
            guestOut.reset();
            guestOut.writeUnshared(g);
            guestOut.flush();
        }
    }

    public boolean isFull() {
        return hostSocket != null && guestSocket != null;
    }

    public boolean startGame() { //TODO : test with mock GameBoard
        Game newGame = new Game(players[0], players[1]);
        Player currentPlayer = Math.random() < 0.5 ? players[0] : players[1];
        int gameFinished = 0; // 0 = game not finished, -1 = draw, 1 = victory //@TODO use enum instead of int
        while(gameFinished == 0) {
            gameFinished = newGame.playTurn(currentPlayer);
            newGame.getGameBoard();
            //TODO send gameBoard to clients for them to display it
            //switch player
            currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
        }
        if (gameFinished == -1) {
            //TODO send draw message to both players
        } else if (gameFinished == 1) {
            //TODO send victory message to currentPlayer
            //TODO send defeat message to the other player
        }
        return true;
    }

}