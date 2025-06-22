package etu.ensicaen.shared.models;

/**
 * Used to signal violations of game rules or
 * incorrect board configurations, such as invalid seed counts
 * or illegal moves.
 */
public class GameBoardException extends RuntimeException {
    /**
     * Constructs a new GameBoardException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public GameBoardException(String message) {
        super(message);
    }
}
