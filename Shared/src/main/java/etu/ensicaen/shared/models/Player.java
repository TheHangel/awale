package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a player in the game.
 * Each player is identified by a username.
 */
public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;

    /**
     * Constructs a Player with the given username.
     *
     * @param username the name of the player
     */
    public Player(String username) {
        this.username = username;
    }

    /**
     * Returns the username of this player.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }
}