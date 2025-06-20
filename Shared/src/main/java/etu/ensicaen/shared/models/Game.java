package etu.ensicaen.shared.models;

import java.io.Serializable;

public class Game implements Serializable {
    private final Player[] players = new Player[2];
    private final PlayerScore[] playerScores = new PlayerScore[2];
    private final GameBoard gameBoard;

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

    public PlayerScore[] getPlayerScores() {
        return playerScores;
    }

    public GameBoard getGameBoard() { return gameBoard; }

    //TODO Check rule 6 avant de faire un coup (apelle une méthode de GameBoard, mais set le resultat dans un cache)
    //      -> si coup qui nourri, le joueur doit choisir parmis les case qui nourrissent (cases possibles dans un cache pour verif rapide)
    //      -> si aucun coup qui nourri, jeu s'arrete + joueur qui devait jouer capture toutes les graines

    //TODO méthode pour init le jeu (game) :
    //      -> créer un plateau de jeu (GameBoard) avec les joueurs (Player) et les graines (Tile)

    //TODO methode jouer un tour (player, index) :
    //      -> simule tout les coup possibles (règle 6) et stocke ceux qui nourissent dans un cache
    //      -> demande au joueur de choisir une case (via la Session qui appelle le serveur ????) : le plus flou pour moi
    //      -> vérifie si c'est un coup valide (règle 6, appartient au joueur et a des graines dedans)
    //      -> distribue les graines de la case choisie + capture si necessaire (methode de GameBoard)
    //      -> vérifie si le joueur a gagné (return true ou false)

    //TODO méthode pour vérifier si le jeu est terminé
    //      -> un joueur a > 25 graines (il gagne)
    //      -> <= 6 graines sur le plateau et aucun joueur >24 graines (match nul)

    //TODO méthode : proposer abandon :
    //      -> <= 10 graines sur le plateau
    //      -> l'autre joueur doit accepter (pareil que le tour, comment on demande au joueur ?)
    //      -> puis se partage les graines restantes (si impair?)
}
