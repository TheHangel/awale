package etu.ensicaen.shared.models;

/**
 * Represents the possible states of a {@link Game}.
 * A game can be in one of the following states:
 *     ONGOING – The game is still in progress.
 *     WIN – The current player has won the game.
 *     LOSE – The current player has lost the game.
 *     DRAW – The game ended in a draw.
 */
public enum GameState {
    /** The game is still in progress. */
    ONGOING,

    /** The current player has won the game. */
    WIN,

    /** The current player has lost the game. */
    LOSE,

    /** The game ended in a draw. */
    DRAW
}
