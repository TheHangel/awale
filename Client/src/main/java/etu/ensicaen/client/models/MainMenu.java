package etu.ensicaen.client.models;

import etu.ensicaen.client.Client;
import etu.ensicaen.shared.models.Game;
import javafx.concurrent.Task;

public class MainMenu {
    public Task<String> host() {
         return new Task<>() {
            @Override
            protected String call() throws Exception {
                return Client.get().host();
            }
        };
    }

    public Task<String> join(String id) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                return Client.get().join(id);
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