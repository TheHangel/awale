package etu.ensicaen.shared.models;

import java.io.Serializable;

public class Node implements Serializable {
    private final Tile tile;
    private Node next;
    private Node prev;

    public Node(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
