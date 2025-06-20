package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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

    public List<Node> getBoard() {
        return board;
    }

    public Node getNodeAt(int index) {
        return board.get(index % BOARD_SIZE);
    }

    public void distributeSeeds(int index, Player player) { //Rule 3
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
    }
}
