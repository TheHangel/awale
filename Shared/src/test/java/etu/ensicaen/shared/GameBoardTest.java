package etu.ensicaen.shared;

import etu.ensicaen.shared.models.GameBoard;
import etu.ensicaen.shared.models.Node;
import etu.ensicaen.shared.models.Player;
import etu.ensicaen.shared.models.Tile;
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
}
