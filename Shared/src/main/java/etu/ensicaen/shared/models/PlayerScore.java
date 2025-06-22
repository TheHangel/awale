package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the score associated with a player.
 * This class is used to track and compare players' scores during the game.
 * It implements {@link Comparable} to allow sorting based on score values.
 */
public class PlayerScore implements Comparable<PlayerScore>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int score = 0;
    private final Player player;

    /**
     * Constructs a {@link PlayerScore} for the given player with an initial score of 0.
     *
     * @param p the player associated with this score
     */
    public PlayerScore(Player p) {
        this.player = p;
    }

    /**
     * Increases the player's score by the specified value.
     *
     * @param value the number of points to add to the score
     */
    public void increase(int value) {
        this.score+= value;
    }

    /**
     * Returns the player associated with this score.
     *
     * @return the player object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the current score of the player.
     *
     * @return the player's score
     */
    public Integer getScore() {
        return this.score;
    }

    /**
     * Compares this {@link PlayerScore} with another based on their scores.
     *
     * @param ps the other {@link PlayerScore} to compare with
     * @return a negative integer, zero, or a positive integer as this score
     *         is less than, equal to, or greater than the specified score
     */
    @Override
    public int compareTo(PlayerScore ps) {
        return this.getScore().compareTo(ps.getScore());
    }
}
