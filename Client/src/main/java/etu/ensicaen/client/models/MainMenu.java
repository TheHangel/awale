package etu.ensicaen.client.models;

import etu.ensicaen.client.Client;
import etu.ensicaen.shared.models.Game;
import javafx.concurrent.Task;

/**
 * Model class for the Main Menu.
 */
public class MainMenu {
    /**
     * Creates a task that sends a request to the server to host a new session.
     *
     * @param username the username of the player who is hosting
     * @return a {@link Task} that returns the session ID when completed
     */
    public Task<String> host(String username) {
         return new Task<>() {
            @Override
            protected String call() throws Exception {
                return Client.get().host(username);
            }
        };
    }

    /**
     * Creates a task that attempts to join an existing session.
     *
     * @param id       the ID of the session to join
     * @param username the username of the player attempting to join
     * @return a {@link Task} that returns the result of the join attempt, usually "JOINED:{id}" or an "ERROR"
     */
    public Task<String> join(String id, String username) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                return Client.get().join(id, username);
            }
        };
    }

    /**
     * Creates a task that sends a request to the server to start the game.
     * This should be called once both players have joined a session.
     *
     * @return a {@link Task} that returns the initialized {@link Game} object
     */
    public Task<Game> play() {
        return new Task<>() {
            @Override
            protected Game call() throws Exception {
                return Client.get().play();
            }
        };
    }
}