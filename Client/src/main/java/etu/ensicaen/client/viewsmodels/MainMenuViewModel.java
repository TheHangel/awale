package etu.ensicaen.client.viewsmodels;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.handlers.ViewHandler;
import etu.ensicaen.client.models.MainMenu;
import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.Leaderboard;
import etu.ensicaen.shared.models.PlayerScore;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;

public class MainMenuViewModel {
    private final MainMenu model;
    private final ViewHandler viewHandler;

    private final BooleanProperty isHost = new SimpleBooleanProperty();

    private final BooleanProperty isWaitingVisible = new SimpleBooleanProperty(false);
    private final BooleanProperty isJoined = new SimpleBooleanProperty(false);

    private final StringProperty sessionId = new SimpleStringProperty();

    private final StringProperty username = new SimpleStringProperty();

    private final ReadOnlyListWrapper<PlayerScore> leaderboard =
            new ReadOnlyListWrapper<>(FXCollections.observableArrayList());


    public MainMenuViewModel(ViewHandler vh) {
        this.model = new MainMenu();
        this.viewHandler = vh;
    }

    public BooleanProperty isWaitingVisibleProperty() {
        return this.isWaitingVisible;
    }
    public BooleanProperty isJoinedProperty() {
        return this.isJoined;
    }

    public StringProperty sessionIdProperty() {
        return this.sessionId;
    }

    public StringProperty usernameProperty() {
        return this.username;
    }

    public ReadOnlyListProperty<PlayerScore> leaderboardProperty() {
        return leaderboard.getReadOnlyProperty();
    }

    public void loadLeaderboard() {
        Task<Leaderboard> task = new Task<>() {
            @Override
            protected Leaderboard call() throws Exception {
                return Client.get().leaderboard();
            }
        };
        task.setOnSucceeded(ev -> {
            Leaderboard lb = task.getValue();
            Platform.runLater(() -> {
                leaderboard.setAll(lb);
            });
        });
        task.setOnFailed(ev -> {
            task.getException().printStackTrace();
        });
        new Thread(task).start();
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
        if(this.username.get().isEmpty()) {
            return;
        }
        Task<String> task = this.model.host(this.username.get());
        task.setOnSucceeded(ev -> {
            String sessionString = task.getValue();
            String sessionId = sessionString.substring(11);
            this.sessionId.set(sessionId);
            this.isWaitingVisible.set(true);
            this.isHost.set(true);
        });
        new Thread(task).start();
    }

    @FXML
    public void onJoin(String id) {
        if(this.username.get().isEmpty()) {
            return;
        }
        Task<String> task = this.model.join(id, this.username.get());
        task.setOnSucceeded(ev -> {
            if(task.getValue().startsWith("ERROR")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Join Error / Erreur pour rejoindre");
                alert.setHeaderText("Failed to join session / Échec pour rejoindre la session");
                alert.setContentText(task.getValue().substring(6));
                alert.showAndWait();
                return;
            }
            this.isJoined.set(true);
            this.isHost.set(false);
        });
        new Thread(task).start();
    }
}
