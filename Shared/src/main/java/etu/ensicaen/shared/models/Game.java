package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Player[] players = new Player[2];
    private final PlayerScore[] playerScores = new PlayerScore[2];
    private final GameBoard gameBoard;

    private GameState gameState;

    private int currentPlayerIndex;

    public GameState getGameState() {
        return gameState;
    }

    //create and init game
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

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    public Player[] getPlayers() { return players; }

    public PlayerScore[] getPlayerScores() { return playerScores; }

    public GameBoard getGameBoard() { return gameBoard; }

    public boolean hasPossibleMoves(Player currentPlayer){ //rule 6
        List<Integer> possibleMoves = gameBoard.getPossibleMoves(currentPlayer);
        return possibleMoves.size() > 0;
    }

    public void handleNoMoreMoves(int currentPlayerIndex) {
        //if no moves can feed the opponent, game stop and current player capture remaining seeds
        playerScores[currentPlayerIndex].increase(gameBoard.takeRemainingSeeds());
        int currentPlayerScore = playerScores[currentPlayerIndex].getScore();
        int opponentScore = playerScores[(currentPlayerIndex+1)%2].getScore();
        this.gameState = currentPlayerScore > opponentScore ? GameState.WIN : GameState.LOSE;
    }

    public boolean isMoveLegal(int currentPlayerIndex, int move){
        Player currentPlayer = players[currentPlayerIndex];
        List<Integer> possibleMoves = gameBoard.getPossibleMoves(currentPlayer);
        Tile tile = gameBoard.getNodeAt(move).getTile();

        return tile.getOwner().equals(currentPlayer) && tile.getSeeds()>0 && possibleMoves.contains(move);
    }

    //apply move (seeds distribution) without checking if legal
    public void playMove(int currentPlayerIndex, int move){
        Player currentPlayer = players[currentPlayerIndex];
        int turnScore = gameBoard.distributeSeeds(move, currentPlayer);
        playerScores[currentPlayerIndex].increase(turnScore);
        this.gameState = checkWinCondition(currentPlayerIndex);
    }

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

    //binding to activate/desactivate forfeit button
    public boolean canForfeit(){
        return gameBoard.countRemainingSeeds() <= 10;
    }

    //onclick on forfeit button
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
