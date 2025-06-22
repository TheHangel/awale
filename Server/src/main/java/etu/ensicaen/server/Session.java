package etu.ensicaen.server;

import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Leaderboard;
import etu.ensicaen.shared.models.Messages;
import etu.ensicaen.shared.models.Player;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Represents a game session between two players.
 * Manages sockets, players, game logic, and communication with clients via ObjectOutputStream.
 */
public class Session {
    private static final boolean test = false;
    private final String id;
    private Socket hostSocket;
    private       Socket guestSocket;
    private final Player[] players = new Player[2];

    private Game currentGame;
    private ObjectOutputStream hostOut;
    private ObjectOutputStream guestOut;

    /**
     * Creates a new session hosted by a player.
     *
     * @param hostSocket the socket of the host player
     * @param username the username of the host player
     */
    public Session(Socket hostSocket, String username) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.hostSocket = hostSocket;
        this.players[0] = new Player(username);
    }

    /**
     * @return the unique session ID
     */
    public String getId() { return id; }

    /**
     * @return the current game instance
     */
    public Game getCurrentGame() { return currentGame; }

    /**
     * Adds a guest player to the session.
     *
     * @param socket the guest player's socket
     * @param username the guest's username
     * @return true if successfully added, false if the session already has a guest
     */
    public synchronized boolean addGuest(Socket socket, String username) {
        if (guestSocket == null) {
            guestSocket = socket;
            this.players[1] = new Player(username);
            return true;
        }
        return false;
    }

    /**
     * Sets the output stream for the host player.
     *
     * @param out the host's ObjectOutputStream
     */
    public synchronized void setHostStream(ObjectOutputStream out) {
        this.hostOut = out;
    }

    /**
     * Sets the output stream for the guest player.
     *
     * @param out the guest's ObjectOutputStream
     */
    public synchronized void setGuestStream(ObjectOutputStream out) {
        this.guestOut = out;
    }

    /**
     * Broadcasts the current game state to both players.
     * If a stream fails, it is invalidated (set to null).
     */
    public synchronized void broadcastGame() {
        if (hostOut != null) {
            try { hostOut.reset(); hostOut.writeUnshared(currentGame); hostOut.flush(); }
            catch(IOException e){ hostOut = null; }
        }
        if (guestOut != null) {
            try { guestOut.reset(); guestOut.writeUnshared(currentGame); guestOut.flush(); }
            catch(IOException e){ guestOut = null; }
        }
    }

    /**
     * Broadcasts a message to both players.
     *
     * @param message the message to send
     * @throws IOException if an I/O error occurs
     */
    public synchronized void broadcast(String message) throws IOException {
        hostOut.reset();
        hostOut.writeUnshared(message);
        hostOut.flush();

        guestOut.reset();
        guestOut.writeUnshared(message);
        guestOut.flush();
    }

    /**
     * Sends a message to a specific player.
     *
     * @param out the output stream of the target player
     * @param message the message to send
     */
    public synchronized void broadcastTo(ObjectOutputStream out, String message) {
        if (out == null) return;
        try {
            out.reset();
            out.writeUnshared(message);
            out.flush();
        } catch (IOException e) {
            if (out == hostOut)   hostOut = null;
            if (out == guestOut)  guestOut = null;
        }
    }

    /**
     * @return true if both host and guest are connected
     */
    public boolean isFull() {
        return hostSocket != null && guestSocket != null;
    }

    /**
     * Initializes a new game instance between the two players.
     * Randomly selects the starting player.
     */
    public void initGame() {
        this.currentGame = new Game(players[0], players[1]);
        this.currentGame.setCurrentPlayerIndex(Math.random() < 0.5 ? 0 : 1);
        if(test) {
            this.setUpForTest();
        }
    }

    /**
     * Initializes a test configuration of the game.
     * Used for debugging or demonstration purposes.
     */
    private void setUpForTest() {
        this.currentGame.setCurrentPlayerIndex(0);
        this.currentGame.getPlayerScores()[0].increase(23);
        this.currentGame.getPlayerScores()[1].increase(3);

        int[] seedDistrib = {6, 3, 4, 0, 1, 2, 2, 1, 1, 2, 2, 0}; //test win
        for (int i = 0; i < seedDistrib.length; i++) {
            this.currentGame.getGameBoard().getNodeAt(i).getTile().setSeeds(seedDistrib[i]);
        }
    }

    /**
     * Handles the move made by a player based on their socket.
     *
     * @param socket the socket of the player making the move
     * @param move the index of the selected tile
     * @throws IOException if an I/O error occurs during communication
     */
    public void handlePlayerInput(Socket socket, int move) throws IOException {
        int playerIndex = socket.equals(hostSocket) ? 0 : 1;
        if(playerIndex == this.currentGame.getCurrentPlayerIndex()) {
            playOneTurn(playerIndex, move);
        }
    }

    /**
     * Executes a player's turn and updates the game state.
     *
     * @param playerIndex the index of the player (0 or 1)
     * @param move the move index
     * @throws IOException if broadcasting fails
     */
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

    /**
     * Updates the leaderboard with the current player score.
     *
     * @param currentPlayerIndex the index of the winning player
     * @return true if it is a new high score
     */
    private boolean handleLeaderboard(int currentPlayerIndex) {
        //Handle Leaderboard
        Leaderboard lb = Server.getLeaderboard();
        boolean newPB = lb.isNewHighScore(this.currentGame.getPlayerScores()[currentPlayerIndex]);
        lb.updateScore(this.currentGame.getPlayerScores()[currentPlayerIndex]);
        Leaderboard.save(lb);
        return newPB;
    }

    /**
     * Checks the current game state and sends messages to the players.
     *
     * @throws IOException if sending messages fails
     */
    public void checkGameStatus() throws IOException {
        int currentPlayerIndex = this.currentGame.getCurrentPlayerIndex();
        switch (this.currentGame.getGameState()) {
            case DRAW :
                this.broadcast(Messages.DRAW_MESSAGE);
                break;
            case WIN: {
                boolean newPB = handleLeaderboard(currentPlayerIndex);

                ObjectOutputStream currentPlayerOut = (currentPlayerIndex == 0) ? this.hostOut : this.guestOut;
                this.broadcastTo(currentPlayerOut, newPB ? Messages.NEW_HIGH_SCORE : Messages.WIN_MESSAGE);
                ObjectOutputStream otherPlayerOut = (currentPlayerIndex == 0) ? this.guestOut : this.hostOut;
                this.broadcastTo(otherPlayerOut, Messages.LOST_MESSAGE);
                break;
            }
            case LOSE: {
                boolean newPB = handleLeaderboard((currentPlayerIndex+1)%2);

                ObjectOutputStream currentPlayerOut = (currentPlayerIndex == 0) ? this.hostOut : this.guestOut;
                this.broadcastTo(currentPlayerOut, Messages.LOST_MESSAGE);
                ObjectOutputStream otherPlayerOut = (currentPlayerIndex == 0) ? this.guestOut : this.hostOut;
                this.broadcastTo(otherPlayerOut, newPB ? Messages.NEW_HIGH_SCORE : Messages.WIN_MESSAGE);
                break;
            }
            case ONGOING:
                //switch player
                this.currentGame.setCurrentPlayerIndex((currentPlayerIndex + 1) % 2);
                //check rule 6
                if (!this.currentGame.hasPossibleMoves(players[currentPlayerIndex])){
                    this.currentGame.handleNoMoreMoves(currentPlayerIndex);

                    boolean newPB = handleLeaderboard(currentPlayerIndex);
                    String winMessage = newPB ? Messages.NEW_HIGH_SCORE : Messages.WIN_MESSAGE;

                    ObjectOutputStream currentPlayerOut = (currentPlayerIndex == 0) ? this.hostOut : this.guestOut;
                    this.broadcastTo(currentPlayerOut, winMessage + "\n" + Messages.CANT_FEED);
                    ObjectOutputStream otherPlayerOut = (currentPlayerIndex == 0) ? this.guestOut : this.hostOut;
                    this.broadcastTo(otherPlayerOut, Messages.LOST_MESSAGE + "\n" + Messages.CANT_FEED);
                }
                break;
        }
    }

    /**
     * @param socket the socket of a player
     * @return the output stream of the other player in the session
     */
    public synchronized ObjectOutputStream getOtherOutputStream(Socket socket) {
        if (socket.equals(hostSocket)) {
            return guestOut;
        } else if (socket.equals(guestSocket)) {
            return hostOut;
        }
        return null;
    }
}