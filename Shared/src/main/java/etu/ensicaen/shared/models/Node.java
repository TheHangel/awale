package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a node in the game board structure.
 * Each node wraps a {@link Tile} and maintains references to its
 * next and previous nodes, forming a circular doubly-linked list.
 */
public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Tile tile;
    private Node next;
    private Node prev;

    /**
     * Constructs a node containing the specified tile.
     *
     * @param tile the tile to associate with this node
     */
    public Node(Tile tile) {
        this.tile = tile;
    }

    /**
     * Returns the tile contained in this node.
     *
     * @return the tile
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Returns the next node in the list.
     *
     * @return the next node
     */
    public Node getNext() {
        return next;
    }

    /**
     * Returns the previous node in the list.
     *
     * @return the previous node
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * Sets the next node reference.
     *
     * @param next the node to set as next
     */
    public void setNext(Node next) {
        this.next = next;
    }

    /**
     * Sets the previous node reference.
     *
     * @param prev the node to set as previous
     */
    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
