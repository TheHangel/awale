package etu.ensicaen.client.viewsmodels;

import etu.ensicaen.client.handlers.ViewHandler;
import etu.ensicaen.client.models.MainMenu;
import etu.ensicaen.shared.models.Game;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.Arrays;

public class MainMenuViewModel {
    private final MainMenu model;
    private final ViewHandler viewHandler;

    private BooleanProperty isHost = new SimpleBooleanProperty();

    public MainMenuViewModel(ViewHandler vh) {
        this.model = new MainMenu();
        this.viewHandler = vh;
    }

    @FXML
    public void onPlay() {
        Task<Game> task = this.model.play();
        task.setOnSucceeded(ev -> {
            Game g = task.getValue();
            try {
                viewHandler.openGame(g, this.isHost);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(ev -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    @FXML
    public void onHost() {
        Task<String> task = this.model.host();
        task.setOnSucceeded(ev -> {
            System.out.println(task.getValue());
            this.isHost.set(true);
        });
        new Thread(task).start();
    }

    @FXML
    public void onJoin(String id) {
        Task<String> task = this.model.join(id);
        task.setOnSucceeded(ev -> {
            System.out.println(task.getValue());
            this.isHost.set(false);
        });
        new Thread(task).start();
    }
}
