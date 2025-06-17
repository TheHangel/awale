package etu.ensicaen.shared;

import etu.ensicaen.shared.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GameBoardTest {
    private GameBoard gameBoard;
    Player player1 = new Player("1", "Player", 20);
    Player player2 = new Player("2", "Player", 20);

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard(player1, player2);
    }

    @Test
    void testBoardInitialization() {
        List<Node> board = gameBoard.getBoard();
        assertEquals(GameBoard.BOARD_SIZE, board.size(), "Board should have 12 tiles");

        for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
            Node node = board.get(i);
            Tile tile = node.getTile();
            assertNotNull(tile, "Tile should not be null");
            assertEquals(4, tile.getSeeds(), "Each tile should have 4 seeds initially");

            if (i < GameBoard.BOARD_SIZE / 2) {
                assertEquals(player1, tile.getOwner(), "First half should belong to Player 1");
            } else {
                assertEquals(player2, tile.getOwner(), "Second half should belong to Player 2");
            }
        }
    }

    @Test
    void testCircularLinking() {
        Node first = gameBoard.getNodeAt(0);
        Node last = gameBoard.getNodeAt(GameBoard.BOARD_SIZE - 1);

        assertEquals(first, last.getNext(), "Last node's next should be the first node (circular next)");
        assertEquals(last, first.getPrev(), "First node's prev should be the last node (circular prev)");
    }

    @Test
    void testGetNodeAt() {
        Node n5 = gameBoard.getNodeAt(5);
        assertNotNull(n5);
        assertEquals(4, n5.getTile().getSeeds());

        // Test circular indexing
        Node n12 = gameBoard.getNodeAt(12); // index 12 % 12 = 0
        assertEquals(gameBoard.getNodeAt(0), n12);
    }

    @Test
    void testTakeAllSeeds() {
        Node node = gameBoard.getNodeAt(3);
        int taken = node.getTile().takeAllSeeds();

        assertEquals(4, taken, "Should take all 4 seeds");
        assertEquals(0, node.getTile().getSeeds(), "Tile should now have 0 seeds");
    }

    @Test
    void testDistributeSeeds() {
        Node startNode = gameBoard.getNodeAt(0);
        Player player = startNode.getTile().getOwner();
        int initialSeeds = startNode.getTile().getSeeds();

        assertEquals(4, initialSeeds, "Start tile should have 4 seeds before distribution");

        gameBoard.distributeSeeds(0, player);

        // Check that seeds were distributed correctly
        for (int i = 1; i < 5; i++) {
            Node node = gameBoard.getNodeAt(i);
            assertEquals(5, node.getTile().getSeeds(), "Next four tiles should have 5 seeds after distribution");
        }
        for(int i = 5; i < GameBoard.BOARD_SIZE; i++) {
            Node node = gameBoard.getNodeAt(i);
            assertEquals(4, node.getTile().getSeeds(), "Remaining tiles should still have 4 seeds");
        }
        assertEquals(0, startNode.getTile().getSeeds(), "Start tile should have 0 seeds after distribution");
    }

    @Test
    void testDistributeFromOpponentTileThrows() {
        int opponentIndex = GameBoard.BOARD_SIZE / 2;
        Player currentPlayer = gameBoard.getNodeAt(0).getTile().getOwner();

        assertNotEquals(currentPlayer, gameBoard.getNodeAt(opponentIndex).getTile().getOwner());

        assertThrows(IllegalArgumentException.class, () -> {
            gameBoard.distributeSeeds(opponentIndex, currentPlayer);
        }, "Should not allow player to distribute seeds from opponent's tile");
    }

    @Test
    void testDistributeFromEmptyTileThrows() {
        int index = 0;
        Player player = gameBoard.getNodeAt(index).getTile().getOwner();
        gameBoard.getNodeAt(index).getTile().takeAllSeeds();

        assertThrows(IllegalArgumentException.class, () -> {
            gameBoard.distributeSeeds(index, player);
        }, "Should not allow distributing seeds from an empty tile");
    }

    @Test
    void testStartTileIsSkippedDuringDistribution() {
        int index = 0;
        Player player = gameBoard.getNodeAt(index).getTile().getOwner();
        Node startNode = gameBoard.getNodeAt(index);
        startNode.getTile().setSeeds(18); //more than a full round

        gameBoard.distributeSeeds(index, player);

        assertEquals(0, startNode.getTile().getSeeds(),
                "Start tile must be empty after sowing, even if full round");
        assertEquals(6, startNode.getNext().getTile().getSeeds(),
                "Tile next to start must have 2 more seeds after full round");
    }
}
