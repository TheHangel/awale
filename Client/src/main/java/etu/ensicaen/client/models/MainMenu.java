package etu.ensicaen.client.models;

import etu.ensicaen.client.Client;
import etu.ensicaen.shared.models.Game;
import javafx.concurrent.Task;

public class MainMenu {
    public Task<String> host(String username) {
         return new Task<>() {
            @Override
            protected String call() throws Exception {
                return Client.get().host(username);
            }
        };
    }

    public Task<String> join(String id, String username) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                return Client.get().join(id, username);
            }
        };
    }

    public Task<Game> play() {
        return new Task<>() {
            @Override
            protected Game call() throws Exception {
                return Client.get().play();
            }
        };
    }
}