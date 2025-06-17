package etu.ensicaen.server;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Player;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class Session {
    private final String id;
    private final Socket hostSocket;
    private       Socket guestSocket;
    private final Player[] players = new Player[2];
    private ArrayList<Game> games;

    public Session(Socket hostSocket) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.games = new ArrayList<>();

        Player hostPlayer = new Player("name player 1"); // @todo send custom username
        // add the host (who created the session) to the session
        this.players[0] = hostPlayer;
        this.hostSocket = hostSocket;
    }

    public String getId() {
        return id;
    }

    public synchronized boolean addGuest(Socket socket) {
        if (guestSocket == null) {
            guestSocket = socket;
            Player guestPlayer = new Player("name player 2"); // @todo send custom username
            this.players[1]= guestPlayer;
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

    public boolean startGame() { //TODO : test with mock GameBoard
        Game newGame = new Game(players[0], players[1]);
        this.games.add(newGame);
        Player currentPlayer = Math.random() < 0.5 ? players[0] : players[1];
        int gameFinished = 0;
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