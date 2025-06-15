package etu.ensicaen.shared.models;

public class Node {
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

    void setNext(Node next) {
        this.next = next;
    }

    void setPrev(Node prev) {
        this.prev = prev;
    }
}
