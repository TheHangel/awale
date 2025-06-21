package etu.ensicaen.server;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Messages;
import etu.ensicaen.shared.models.Player;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class Session {
    private static final boolean test = false;
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

    public synchronized void broadcast(String message) throws IOException {
        hostOut.reset();
        hostOut.writeUnshared(message);
        hostOut.flush();

        guestOut.reset();
        guestOut.writeUnshared(message);
        guestOut.flush();
    }

    public synchronized void broadcastTo(ObjectOutputStream out, String message) throws IOException {
        out.reset();
        out.writeUnshared(message);
        out.flush();
    }

    public boolean isFull() {
        return hostSocket != null && guestSocket != null;
    }

    public void initGame() {
        this.currentGame = new Game(players[0], players[1]);
        this.currentPlayerIndex = Math.random() < 0.5 ? 0 : 1;
        this.currentGame.setCurrentPlayerIndex(this.currentPlayerIndex);

        if(test) {
            this.setUpForTest();
        }
    }

    private void setUpForTest() {
        this.currentPlayerIndex = 0;
        this.currentGame.getPlayerScores()[0].increase(0);
        this.currentGame.getPlayerScores()[1].increase(0);

        int[] seedDistrib = {0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0}; //test skip tile
        for (int i = 0; i < seedDistrib.length; i++) {
            this.currentGame.getGameBoard().getNodeAt(i).getTile().setSeeds(seedDistrib[i]);
        }
    }

    public void handlePlayerInput(Socket socket, int move) throws IOException {
        int playerIndex = socket.equals(hostSocket) ? 0 : 1;
        if(playerIndex == currentPlayerIndex){
            playOneTurn(playerIndex, move);
        }
    }

    public void playOneTurn(int playerIndex, int move) throws IOException {
        Player currentPlayer = players[playerIndex];
        if(currentGame.hasPossibleMoves(currentPlayer)) {
            if (!currentGame.isMoveLegal(playerIndex, move)) {
                this.broadcast(Messages.ILLEGAL_MESSAGE);
                return; //player need to choose another move
            } else {
                this.currentGame.playMove(playerIndex, move);
            }
        }
        else { //no possible moves
            this.currentGame.handleNoMoreMoves(playerIndex);
        }

        //sending new board to players
        this.broadcastGame();
        this.checkGameStatus();
    }

    public void checkGameStatus() throws IOException {
        switch (this.currentGame.getGameState()) {
            case DRAW :
                this.broadcast(Messages.DRAW_MESSAGE);
                break;
            case WIN: {
                ObjectOutputStream currentPlayerOut = (currentPlayerIndex == 0) ? this.hostOut : this.guestOut;
                this.broadcastTo(currentPlayerOut, Messages.WIN_MESSAGE);
                ObjectOutputStream otherPlayerOut = (currentPlayerIndex == 0) ? this.guestOut : this.hostOut;
                this.broadcastTo(otherPlayerOut, Messages.LOST_MESSAGE);
                break;
            }
            case LOSE: {
                ObjectOutputStream currentPlayerOut = (currentPlayerIndex == 0) ? this.hostOut : this.guestOut;
                this.broadcastTo(currentPlayerOut, Messages.LOST_MESSAGE);
                ObjectOutputStream otherPlayerOut = (currentPlayerIndex == 0) ? this.guestOut : this.hostOut;
                this.broadcastTo(otherPlayerOut, Messages.WIN_MESSAGE);
                break;
            }
            case ONGOING:
                //switch player
                currentPlayerIndex = (currentPlayerIndex + 1) % 2;
                this.currentGame.setCurrentPlayerIndex(this.currentPlayerIndex);
                //check rule 6
                if (!this.currentGame.hasPossibleMoves(players[currentPlayerIndex])){
                    this.currentGame.handleNoMoreMoves(currentPlayerIndex);

                    ObjectOutputStream currentPlayerOut = (currentPlayerIndex == 0) ? this.hostOut : this.guestOut;
                    this.broadcastTo(currentPlayerOut, "NO MOVE TO FEED OPPONENT : VICTORY");
                    ObjectOutputStream otherPlayerOut = (currentPlayerIndex == 0) ? this.guestOut : this.hostOut;
                    this.broadcastTo(otherPlayerOut, "NO MOVE TO FEED OPPONENT : DEFEAT");
                }
                break;
        }
    }

    public ObjectOutputStream getOtherOutputStream(Socket socket) {
        if (socket.equals(hostSocket)) {
            return guestOut;
        } else if (socket.equals(guestSocket)) {
            return hostOut;
        }
        return null;
    }
}