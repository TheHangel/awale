package etu.ensicaen.shared.models;

/**
 * Exception thrown to indicate an error during the initialization
 *  * or execution of a {@link Game}.
 * Used to signal invalid game state,
 * such as null players or attempts to start a game with identical players
 */
public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }
}
