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

/**
 * ViewModel for the Main Menu.
 * Handles the logic for hosting and joining games, as well as loading the leaderboard.
 */
public class MainMenuViewModel {
    private final MainMenu model;
    private final ViewHandler viewHandler;

    /**
     * Indicates whether the current user is hosting a game.
     */
    private final BooleanProperty isHost = new SimpleBooleanProperty();

    /**
     * Indicates whether the waiting message should be visible (i.e., host is waiting for a guest).
     */
    private final BooleanProperty isWaitingVisible = new SimpleBooleanProperty(false);

    /**
     * Indicates whether the user has successfully joined a session.
     */
    private final BooleanProperty isJoined = new SimpleBooleanProperty(false);

    /**
     * Stores the ID of the current session.
     */
    private final StringProperty sessionId = new SimpleStringProperty();

    /**
     * Stores the username entered by the user.
     */
    private final StringProperty username = new SimpleStringProperty();

    /**
     * List of player scores shown in the leaderboard.
     */
    private final ReadOnlyListWrapper<PlayerScore> leaderboard =
            new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    /**
     * Constructor for MainMenuViewModel.
     *
     * @param vh the ViewHandler to manage views
     */
    public MainMenuViewModel(ViewHandler vh) {
        this.model = new MainMenu();
        this.viewHandler = vh;
    }

    /**
     * Returns the property indicating whether the user is waiting for another player.
     *
     * @return BooleanProperty for waiting visibility
     */
    public BooleanProperty isWaitingVisibleProperty() {
        return this.isWaitingVisible;
    }

    /**
     * Returns the property indicating whether the user has joined a session.
     *
     * @return BooleanProperty for join state
     */
    public BooleanProperty isJoinedProperty() {
        return this.isJoined;
    }

    /**
     * Returns the session ID property.
     *
     * @return StringProperty containing the session ID
     */
    public StringProperty sessionIdProperty() {
        return this.sessionId;
    }

    /**
     * Returns the username property.
     *
     * @return StringProperty containing the username
     */
    public StringProperty usernameProperty() {
        return this.username;
    }

    /**
     * Returns the leaderboard as a read-only property.
     *
     * @return ReadOnlyListProperty of PlayerScore
     */
    public ReadOnlyListProperty<PlayerScore> leaderboardProperty() {
        return leaderboard.getReadOnlyProperty();
    }

    /**
     * Loads the leaderboard from the server asynchronously
     * and updates the internal observable list on the JavaFX thread.
     */
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

    /**
     * Starts a new game if the session is ready and navigates to the game view.
     */
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

    /**
     * Hosts a new session on the server using the current username.
     * Updates UI properties upon success.
     */
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

    /**
     * Attempts to join a session with the given ID using the current username.
     * Displays an alert on failure.
     *
     * @param id the session ID to join
     */
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
