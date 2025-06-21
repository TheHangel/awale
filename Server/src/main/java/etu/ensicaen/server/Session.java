package etu.ensicaen.server;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Player;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class Session {
    private final String id;
    private final Socket hostSocket;
    private       Socket guestSocket;
    private final Player[] players = new Player[2];
    private int currentPlayerIndex;

    private Game currentGame;
    private ObjectOutputStream hostOut;
    private ObjectOutputStream guestOut;

    public Session(Socket hostSocket) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.hostSocket = hostSocket;
        this.players[0] = new Player("Player 1");
    }

    public String getId() { return id; }

    public Game getCurrentGame() { return currentGame; }

    public synchronized boolean addGuest(Socket socket) {
        if (guestSocket == null) {
            guestSocket = socket;
            this.players[1] = new Player("Player 2");
            return true;
        }
        return false;
    }

    public synchronized void setHostStream(ObjectOutputStream out) {
        this.hostOut = out;
    }

    public synchronized void setGuestStream(ObjectOutputStream out) {
        this.guestOut = out;
    }

    public synchronized void broadcastGame() throws IOException {
        if (hostOut != null) {
            hostOut.reset();
            hostOut.writeUnshared(this.currentGame);
            hostOut.flush();
        }
        if (guestOut != null) {
            guestOut.reset();
            guestOut.writeUnshared(this.currentGame);
            guestOut.flush();
        }
    }

    public boolean isFull() {
        return hostSocket != null && guestSocket != null;
    }

    public void initGame() {
        this.currentGame = new Game(players[0], players[1]);
        this.currentPlayerIndex = Math.random() < 0.5 ? 0 : 1;
    }

    public void handlePlayerInput(int playerIndex, int move){
        if(playerIndex == currentPlayerIndex){
            playOneTurn(playerIndex, move);
        }
    }

    public void playOneTurn(int playerIndex, int move) {
        Player currentPlayer = players[playerIndex];
        if(currentGame.hasPossibleMoves(currentPlayer)) {
            if (!currentGame.isMoveLegal(playerIndex, move)) {
                //TODO Broadcast "choose another move"
            } else {
                this.currentGame.playMove(playerIndex, move);
            }
        }
        else { //no possible moves
            this.currentGame.handleNoMoreMoves(playerIndex);
        }

        try {
            //sending new board to players
            broadcastGame();
            this.checkGameStatus();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkGameStatus() {
        switch (this.currentGame.getGameState()) {
            case DRAW :
                //TODO send draw message to both players
                break;
            case WIN:
                //TODO send victory message to currentPlayer
                //TODO send defeat message to the other player
                break;
            case LOSE:
                //TODO Same as win but opposite
                break;
            case ONGOING:
                //switch player
                currentPlayerIndex = (currentPlayerIndex + 1) % 2;
                break;
        }
    }

}