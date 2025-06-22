package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Awalé game board. It is composed of 12 {@link Node} instances,
 * each containing a {@link Tile} that belongs to one of the two players.
 * Handles seed distribution, capturing, and move validation according to the game rules.
 */
public class GameBoard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Total number of tiles on the board (must be even). */
    public static final int BOARD_SIZE = 12;

    /** Total number of seeds initially in the game. */
    public static final int SEEDS_NUMBER = 48;

    private final List<Node> board;

    /**
     * Constructs a new game board and initializes it with seeds and player ownership.
     * Links the board nodes in a circular doubly-linked list.
     *
     * @param player1 The first player (owns the first half of the board).
     * @param player2 The second player (owns the second half of the board).
     * @throws GameBoardException if board size is not even or seeds are not divisible.
     */
    public GameBoard(Player player1, Player player2) { //Rule 2
        if(BOARD_SIZE % 2 != 0) {
            throw new GameBoardException("Board size must be even.");
        }
        if(SEEDS_NUMBER % BOARD_SIZE != 0) {
            throw new GameBoardException("Number of seeds must be divisible by the board size.");
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

    /**
     * Private constructor to create a deep copy of a game board.
     *
     * @param other The board to copy.
     */
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

    /**
     * Returns the full list of board nodes.
     *
     * @return A list of {@link Node} instances.
     */
    public List<Node> getBoard() {
        return board;
    }

    /**
     * Returns the node at the specified index, with circular indexing.
     *
     * @param index Index of the node.
     * @return The {@link Node} at the given index.
     */
    public Node getNodeAt(int index) {
        return board.get(index % BOARD_SIZE);
    }

    /**
     * Distributes seeds starting from a tile and attempts to capture seeds at the end.
     * Implements Rules 3, 4, 5, and 7.
     *
     * @param index Index of the tile to start distribution from.
     * @param player The player performing the move.
     * @return Number of seeds captured (may be 0).
     * @throws GameBoardException if the move is invalid.
     */
    public int distributeSeeds(int index, Player player) { //Rule 3
        int score = 0;
        Node startNode = getNodeAt(index);
        if (startNode.getTile().getOwner() != player) {
            throw new GameBoardException("Cannot distribute seeds from a tile that does not belong to the player.");
        }

        int seedsToDistribute = startNode.getTile().takeAllSeeds();
        if (seedsToDistribute == 0) {
            throw new GameBoardException("Seeds to distribute cannot be zero.");
        }

        Node nextNode = startNode;
        while (seedsToDistribute > 0) {
            nextNode = nextNode.getNext();
            if (nextNode != startNode) { //skip the tile where seeds were taken (rule 5)
                nextNode.getTile().addSeed();
                seedsToDistribute--;
            }
        }

        int endIndex = this.board.indexOf(nextNode);
        if(! willCaptureEverything(endIndex, player)) { // rule 7 : if the move captures all seeds, do not capture anything
            return captureSeeds(endIndex, player);
        }
        return score;
    }

    /**
     * Simulates a capture from the end tile and checks if it would result
     * in capturing all opponent seeds (Rule 7).
     *
     * @param endNodeIdx Index of the ending tile.
     * @param player The player performing the capture.
     * @return true if the move would capture all opponent seeds.
     */
    public boolean willCaptureEverything(int endNodeIdx, Player player){
        GameBoard boardCopy = new GameBoard(this);
        boardCopy.captureSeeds(endNodeIdx, player); // simulate capture

        int seedsRemaining = 0;
        for (Node node : boardCopy.getBoard()) {
            if (node.getTile().getOwner() != player) { // Check opponent's tiles
                seedsRemaining+= node.getTile().getSeeds();
            }
        }
        return seedsRemaining == 0;
    }

    /**
     * Captures seeds from the board starting from the given index.
     * Implements Rule 4. Doesn't check for rule 7.
     *
     * @param endTileIndex Index of the last tile where a seed was placed.
     * @param player The player capturing seeds.
     * @return Number of seeds captured.
     */
    public int captureSeeds(int endTileIndex, Player player) {
        Node currentNode = getNodeAt(endTileIndex);
        if (currentNode.getTile().getOwner() == player) {
            return 0; // Don't capture seeds from a tile that belongs to the player
        }

        int capturedSeeds = 0;
        while((currentNode.getTile().getSeeds() == 2 || currentNode.getTile().getSeeds() == 3)
                && currentNode.getTile().getOwner() != player) {
            capturedSeeds += currentNode.getTile().takeAllSeeds();
            currentNode = currentNode.getPrev();
        }
        return capturedSeeds;
    }

    /**
     * Computes all legal moves for the player, Rule 6.
     *
     * @param player The player whose turn it is.
     * @return A list of indices representing valid moves.
     */
    public ArrayList<Integer> getPossibleMoves(Player player) {
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

    /**
     * Checks whether the opponent of the given player has any seeds left.
     *
     * @param currentPlayer The player to check against.
     * @return true if opponent has at least one seed.
     */
    private boolean opponentHasSeeds(Player currentPlayer) {
        for (Node node : board) {
            if (node.getTile().getOwner() != currentPlayer && node.getTile().getSeeds() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all remaining seeds on the board and returns the total number taken.
     * Used at the end of the game (Rule 8).
     *
     * @return Total number of seeds taken.
     */
    public int takeRemainingSeeds() {
        int totalSeeds = 0;
        for (Node node : board) {
            totalSeeds += node.getTile().takeAllSeeds();
        }
        return totalSeeds;
    }

    /**
     * Counts the total number of seeds currently on the board.
     * Used to determine endgame conditions (Rule 8).
     *
     * @return Number of seeds on the board.
     */
    public int countRemainingSeeds() {
        int totalSeeds = 0;
        for (Node node : board) {
            totalSeeds += node.getTile().getSeeds();
        }
        return totalSeeds;
    }
}