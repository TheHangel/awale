package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Player[] players = new Player[2];
    private final PlayerScore[] playerScores = new PlayerScore[2];
    private final GameBoard gameBoard;

    //TODO GameState global avec un getter

    private List<Integer> possibleMovesCache;

    //create and init game
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
    public Player[] getPlayers() { return players; }

    public PlayerScore[] getPlayerScores() { return playerScores; }

    public GameBoard getGameBoard() { return gameBoard; }

    public boolean HasPossibleMoves(Player currentPlayer){ //rule 6
        List<Integer> possibleMoves = gameBoard.getPossibleMoves(currentPlayer); //TODO mettre dans un cache ? cache à effacer quand le coup est bon
        return possibleMoves.size() > 0;
    }

    public void handleNoMoreMoves(int currentPlayerIndex) {
        //-> si aucun coup qui nourri, jeu s'arrete + joueur qui devait jouer capture toutes les graines
        playerScores[currentPlayerIndex].increase(gameBoard.takeRemainingSeeds());
        int currentPlayerScore = playerScores[currentPlayerIndex].getScore();
        int opponentScore = playerScores[(currentPlayerIndex+1)%2].getScore();
        return currentPlayerScore > opponentScore ?
                GameState.WIN : GameState.LOSE; //TODO changer la var globale
    }

    public boolean isMoveLegal(int currentPlayerIndex, int move){
        Player currentPlayer = players[currentPlayerIndex];
        List<Integer> possibleMoves = gameBoard.getPossibleMoves(currentPlayer);
        Tile tile = gameBoard.getNodeAt(move).getTile();

        return tile.getOwner().equals(currentPlayer) && tile.getSeeds()>0 && possibleMoves.contains(move);
    }

    //apply move (seeds distribution) without checking if legal
    public GameState playMove(int currentPlayerIndex, int move){
        Player currentPlayer = players[currentPlayerIndex];
        int turnScore = gameBoard.distributeSeeds(move, currentPlayer);
        playerScores[currentPlayerIndex].increase(turnScore);
        return checkWinCondition(currentPlayerIndex); //TODO update global variable instead of return
    }

    public GameState checkWinCondition(int currentPlayerIndex){ //handles rule 8
        int currentPlayerScore = playerScores[currentPlayerIndex].getScore();
        int opponentScore = playerScores[(currentPlayerIndex+1)%2].getScore();

        if(currentPlayerScore > 25){
            return GameState.WIN;
        }
        if(currentPlayerScore < 25 && opponentScore < 25 && gameBoard.countRemainingSeeds() <= 6){
            return GameState.DRAW;
        }
        return GameState.ONGOING;
    }

    //TODO méthode : proposer abandon :
    //      -> <= 10 graines sur le plateau
    //      -> l'autre joueur doit accepter (pareil que le tour, comment on demande au joueur ?)
    //      -> puis se partage les graines restantes (si impair?)
    public boolean canForfeit(int currentPlayerIndex){
        return gameBoard.countRemainingSeeds() <= 10;
    }

    public boolean handleForfeit(Player currentPlayer){ //onclick sur forfeit
        //-> demander à l'autre joueur s'il veut forfeit
        //si il accepte
        int seedsRemaining = gameBoard.takeRemainingSeeds();
        for(PlayerScore playerScore : playerScores){
            playerScore.increase(seedsRemaining/2);
        }
        //sinon on fait rien -> GameState en Abandoned (gagnant ou perdant selon le score des 2)
        return false;
    }
}
