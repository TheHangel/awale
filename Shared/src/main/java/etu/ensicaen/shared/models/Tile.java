package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a single tile on the game board.
 * Each tile holds a number of seeds and is owned by a specific player.
 */
public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** The number of seeds currently on the tile. */
    private int seeds;

    /** The player who owns this tile. */
    private final Player owner;

    /**
     * Constructs a {@link Tile} with the given number of seeds and owner.
     *
     * @param seeds the initial number of seeds in the tile
     * @param owner the player who owns the tile
     */
    public Tile(int seeds, Player owner) {
        this.seeds = seeds;
        this.owner = owner;
    }

    /**
     * Returns the player who owns this tile.
     *
     * @return the tile's owner
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Returns the current number of seeds on the tile.
     *
     * @return the number of seeds
     */
    public int getSeeds() {
        return seeds;
    }

    /**
     * Sets the number of seeds on the tile.
     *
     * @param seeds the new seed count
     */
    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    /**
     * Increments the number of seeds on the tile by one.
     */
    public void addSeed() {
        this.seeds++;
    }

    /**
     * Removes and returns all seeds from the tile.
     *
     * @return the number of seeds that were on the tile before removal
     */
    public int takeAllSeeds(){
        int temp = seeds;
        seeds = 0;
        return temp;
    }
}
