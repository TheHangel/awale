package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a complete Awale game session between two players.
 * Handles the board state, scores, current player, and game rules logic.
 */
public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Player[] players = new Player[2];
    private final PlayerScore[] playerScores = new PlayerScore[2];
    private final GameBoard gameBoard;

    private GameState gameState;

    private int currentPlayerIndex;

    /**
     * Constructs a new Game instance between two distinct players.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     * @throws GameException if any player is null or both players are the same.
     */
    public Game(Player player1, Player player2) {
        //handle player
        if (player1 == null || player2 == null) {
            throw new GameException("Players cannot be null.");
        }
        if (player1.equals(player2)) {
            throw new GameException("Players must be different.");
        }

        this.players[0] = player1;
        this.players[1] = player2;
        this.playerScores[0] = new PlayerScore(player1);
        this.playerScores[1] = new PlayerScore(player2);

        //handle game board
        this.gameBoard = new GameBoard(player1, player2);
        this.gameState = GameState.ONGOING;
    }

    /**
     * Returns the current game state.
     *
     * @return The current {@link GameState}.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the index of the player whose turn it is.
     *
     * @param currentPlayerIndex 0 for host, 1 for guest.
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    /**
     * Returns the index of the current player.
     *
     * @return The index (0 or 1) of the current player.
     */
    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    /**
     * Returns the two players of the game.
     *
     * @return An array containing both players.
     */
    public Player[] getPlayers() { return players; }

    /**
     * Returns the scores of the two players.
     *
     * @return An array containing the scores of both players.
     */
    public PlayerScore[] getPlayerScores() { return playerScores; }

    /**
     * Returns the game board.
     *
     * @return The {@link GameBoard} instance.
     */
    public GameBoard getGameBoard() { return gameBoard; }

    /**
     * Checks if the given player has any legal moves left (Rule 6).
     *
     * @param currentPlayer The player to check.
     * @return true if the player has at least one legal move.
     */
    public boolean hasPossibleMoves(Player currentPlayer){
        List<Integer> possibleMoves = gameBoard.getPossibleMoves(currentPlayer);
        return possibleMoves.size() > 0;
    }

    /**
     * Handles the case when the current player has no more moves that can feed the opponent.
     * Captures remaining seeds and ends the game.
     *
     * @param currentPlayerIndex Index of the current player.
     */
    public void handleNoMoreMoves(int currentPlayerIndex) {
        //if no moves can feed the opponent, game stop and current player capture remaining seeds
        playerScores[currentPlayerIndex].increase(gameBoard.takeRemainingSeeds());
        int currentPlayerScore = playerScores[currentPlayerIndex].getScore();
        int opponentScore = playerScores[(currentPlayerIndex+1)%2].getScore();
        this.gameState = currentPlayerScore > opponentScore ? GameState.WIN : GameState.LOSE;
    }

    /**
     * Checks whether a move is legal for the current player.
     *
     * @param currentPlayerIndex Index of the current player.
     * @param move The pit index selected by the player.
     * @return true if the move is legal.
     */
    public boolean isMoveLegal(int currentPlayerIndex, int move){
        Player currentPlayer = players[currentPlayerIndex];
        List<Integer> possibleMoves = gameBoard.getPossibleMoves(currentPlayer);
        Tile tile = gameBoard.getNodeAt(move).getTile();

        return tile.getOwner().equals(currentPlayer) && tile.getSeeds()>0 && possibleMoves.contains(move);
    }

    /**
     * Applies the given move by the current player.
     * Distributes seeds, updates score, and checks win conditions.
     * This method assumes the move is legal.
     *
     * @param currentPlayerIndex Index of the current player.
     * @param move The index of the pit to play.
     */
    public void playMove(int currentPlayerIndex, int move){
        Player currentPlayer = players[currentPlayerIndex];
        int turnScore = gameBoard.distributeSeeds(move, currentPlayer);
        playerScores[currentPlayerIndex].increase(turnScore);
        this.gameState = checkWinCondition(currentPlayerIndex);
    }

    /**
     * Evaluates the game state after a move to check for win or draw.
     * (Rule 8).
     *
     * @param currentPlayerIndex The player who just played.
     * @return The updated {@link GameState}.
     */
    public GameState checkWinCondition(int currentPlayerIndex){ //handles rule 8
        int currentPlayerScore = playerScores[currentPlayerIndex].getScore();
        int opponentScore = playerScores[(currentPlayerIndex+1)%2].getScore();

        if(currentPlayerScore >= 25){
            return GameState.WIN;
        }
        if(currentPlayerScore < 25 && opponentScore < 25 && gameBoard.countRemainingSeeds() <= 6){
            return GameState.DRAW;
        }
        return GameState.ONGOING;
    }

    /**
     * Checks whether the player is allowed to forfeit (used for button enable/disable).
     * A forfeit is only allowed when the remaining seeds are fewer than or equal to 10.
     *
     * @return true if forfeiting is allowed.
     */
    public boolean canForfeit(){
        return gameBoard.countRemainingSeeds() <= 10;
    }

    /**
     * Handles a player's forfeit.
     * Splits the remaining seeds between the two players and determines the winner.
     */
    public void handleForfeit(){
        int seedsRemaining = gameBoard.takeRemainingSeeds();
        for (PlayerScore playerScore : playerScores) {
            playerScore.increase(seedsRemaining / 2);
        }
        int currentPlayerScore = playerScores[currentPlayerIndex].getScore();
        int opponentScore = playerScores[(currentPlayerIndex + 1) % 2].getScore();

        this.gameState = currentPlayerScore > opponentScore ? GameState.WIN : GameState.LOSE;
    }
}
