package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Player[] players = new Player[2];
    private final PlayerScore[] playerScores = new PlayerScore[2];
    private GameBoard gameBoard;

    public Game(Player player1, Player player2) {
        //handle player
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Players cannot be null.");
        }
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("Players must be different.");
        }

        this.players[0] = player1;
        this.players[1] = player2;
        this.playerScores[0] = new PlayerScore(player1);
        this.playerScores[1] = new PlayerScore(player2);

        //handle game board
        this.gameBoard = new GameBoard(player1, player2);
    }

    public Player[] getPlayers() {
        return players;
    }

    public PlayerScore[] getPlayerScores() {
        return playerScores;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}