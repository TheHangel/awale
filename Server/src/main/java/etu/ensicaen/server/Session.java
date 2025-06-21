package etu.ensicaen.server;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.GameState;
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
    //TODO current player global

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

    public void initGame() { //TODO : test with mock GameBoard
        Game newGame = new Game(players[0], players[1]);
        int currentPlayerIndex = Math.random() < 0.5 ? 0 : 1; //TODO variables
        GameState gameStatus = GameState.ONGOING; //TODO variables
    }

    public void handlePlayerInput(int playerIndex, int move){
        //if c'est a lui de jouer avec var globale
            //play one turn(Player player, int move)
        //sinon
            //rien
    }

    public void playOneTurn(Player player, int move) {
        //check hasPossibleMoves(player) (dans Game)
            //if move pas legal
                //broadcast : rechoisi un move
            //else
                //playMove(playerIndex + move) -> change le gameStatus en Win, Draw ou Ongoing

        //si y'a pas de possibleMoves
            //appelle méthode handleNoMoreMoves -> fin de partie -> change le GameStatus en Win ou Lose

        //fin si

        //broadcast le game board modifié
        //checkGameStatus()
    }

    public void checkGameStatus() {
        switch (gameStatus) {
            case DRAW :
                //TODO send draw message to both players
                break;
            case WIN:
                //TODO send victory message to currentPlayer
                //TODO send defeat message to the other player
                break;
            case LOSE: //TODO check if possible to lose during your turn
                //TODO Same as win but opposite
                break;
            case ABANDONED:
                //TODO ask abandon or display end game here ?
                break;
            case ONGOING:
                //switch player
                currentPlayerIndex = (currentPlayerIndex + 1) % 2;
                break;
        }
    }

}