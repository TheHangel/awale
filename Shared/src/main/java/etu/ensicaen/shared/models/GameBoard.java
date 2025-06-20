package etu.ensicaen.shared.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable {
    public static final int BOARD_SIZE = 12;
    public static final int SEEDS_NUMBER = 48;

    private final List<Node> board;

    public GameBoard(Player player1, Player player2) { //Rule 2
        //TODO custom exceptions
        if(BOARD_SIZE % 2 != 0) {
            throw new IllegalArgumentException("Board size must be even.");
        }
        if(SEEDS_NUMBER % BOARD_SIZE != 0) {
            throw new IllegalArgumentException("Number of seeds must be divisible by the board size.");
        }
        int seedsPerTile = SEEDS_NUMBER / BOARD_SIZE;
        board = new ArrayList<>(BOARD_SIZE);

        for(int i = 0; i < BOARD_SIZE/2; i++) {
            board.add(new Node(new Tile(seedsPerTile, player1)));
        }
        for(int i = BOARD_SIZE/2; i < BOARD_SIZE; i++) {
            board.add(new Node(new Tile(seedsPerTile, player2)));
        }

        // Link nodes together (circular double linked list style)
        for (int i = 0; i < BOARD_SIZE; i++) {
            Node current = board.get(i);
            Node next = board.get((i + 1) % BOARD_SIZE);
            Node prev = board.get((i - 1 + BOARD_SIZE) % BOARD_SIZE);

            current.setNext(next);
            current.setPrev(prev);
        }
    }

    private GameBoard(GameBoard other) {
        this.board = new ArrayList<>(other.board.size());
        for (Node node : other.board) {
            this.board.add(new Node(new Tile(node.getTile().getSeeds(), node.getTile().getOwner())));
        }

        // Link nodes together (circular double linked list style)
        for (int i = 0; i < BOARD_SIZE; i++) {
            Node current = this.board.get(i);
            Node next = this.board.get((i + 1) % BOARD_SIZE);
            Node prev = this.board.get((i - 1 + BOARD_SIZE) % BOARD_SIZE);

            current.setNext(next);
            current.setPrev(prev);
        }
    }

    public List<Node> getBoard() {
        return board;
    }

    public Node getNodeAt(int index) {
        return board.get(index % BOARD_SIZE);
    }

    public int distributeSeeds(int index, Player player) { //Rule 3
        int score = 0;
        Node startNode = getNodeAt(index);
        if (startNode.getTile().getOwner() != player) {
            throw new IllegalArgumentException("Cannot distribute seeds from a tile that does not belong to the player.");
        }

        int seedsToDistribute = startNode.getTile().takeAllSeeds();
        if (seedsToDistribute == 0) {
            throw new IllegalArgumentException("Seeds to distribute cannot be zero.");
        }

        Node nextNode = startNode;
        while (seedsToDistribute > 0) {
            nextNode = nextNode.getNext();
            if (nextNode != startNode) { //skip the tile where seeds were taken (rule 5)
                nextNode.getTile().addSeed();
                seedsToDistribute--;
            }
        }

        if(! willCaptureEverything(index, player)) { // rule 7 : if the move captures all seeds, do not capture anything
            return captureSeeds(index, player);
        }
        return score;
    }

    //on a board that as all the seeds moods
    public boolean willCaptureEverything(int startNodeIdx, Player player){ // handles rule 7
        GameBoard boardCopy = new GameBoard(this);
        boardCopy.captureSeeds(startNodeIdx, player); // simulate capture

        int seedsRemaining = 0;
        for (Node node : boardCopy.getBoard()) {
            if (node.getTile().getOwner() != player) { // Check opponent's tiles
                seedsRemaining+= node.getTile().getSeeds();
            }
        }
        return seedsRemaining == 0;
    }

    //doesn't check for rule 7
    public int captureSeeds(int index, Player player) { // handles rule 4
        Node currentNode = getNodeAt(index);
        if (currentNode.getTile().getOwner() == player) {
            return 0; // Don't capture seeds from a tile that belongs to the player
        }

        int capturedSeeds = 0;
        while(currentNode.getTile().getSeeds() == 2 || currentNode.getTile().getSeeds() == 3
                && currentNode.getTile().getOwner() != player) {
            capturedSeeds += currentNode.getTile().takeAllSeeds();
            currentNode = currentNode.getPrev();
        }
        return capturedSeeds;
    }

    public ArrayList<Integer> getPossibleMoves(Player player) { //checks rule 6
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        boolean opponentHasSeeds = this.opponentHasSeeds(player);

        for (int i = 0; i < BOARD_SIZE; i++) {
            Tile tile = this.getNodeAt(i).getTile();
            if(tile.getOwner() == player && tile.getSeeds() > 0) { //every tile they can choose from
                if(opponentHasSeeds){
                    possibleMoves.add(i);
                }
                else{
                    //if the opponent has no seeds, only allow moves that gives seeds to the opponnent
                    GameBoard boardCopy = new GameBoard(this);
                    boardCopy.distributeSeeds(i, player);
                    if(boardCopy.opponentHasSeeds(player)) {
                        possibleMoves.add(i);
                    }
                }
            }
        }
        return possibleMoves;
    }

    private boolean opponentHasSeeds(Player currentPlayer) {
        for (Node node : board) {
            if (node.getTile().getOwner() != currentPlayer && node.getTile().getSeeds() > 0) {
                return true;
            }
        }
        return false;
    }

    public int takeRemainingSeeds() { //handles rule 8
        int totalSeeds = 0;
        for (Node node : board) {
            totalSeeds += node.getTile().takeAllSeeds();
        }
        return totalSeeds;
    }
}
//TODO replace index by Node in methods ?