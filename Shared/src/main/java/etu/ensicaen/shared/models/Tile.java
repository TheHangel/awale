package etu.ensicaen.shared.models;

import java.io.Serializable;

public class Tile implements Serializable {
    private int seeds;
    private final Player owner;

    public Tile(int seeds, Player owner) {
        this.seeds = seeds;
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    public void addSeed() {
        this.seeds++;
    }

    public int takeAllSeeds(){
        int temp = seeds;
        seeds = 0;
        return temp;
    }
}
