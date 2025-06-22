package etu.ensicaen.client.viewsmodels;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.handlers.ViewHandler;
import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.GameBoard;
import etu.ensicaen.shared.models.Node;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the game view, responsible for managing the game state and
 * providing properties to the view.
 */
public class GameViewModel {
    /**
     * The model representing the current game state.
     */
    private Game model;
    /**
     * The view handler to manage view transitions.
     */
    private final ViewHandler viewHandler;

    private final BooleanProperty isHost;
    private final List<IntegerProperty> seedCounts = new ArrayList<>();
    private final StringProperty playerNameLeft   = new SimpleStringProperty();
    private final StringProperty playerNameRight  = new SimpleStringProperty();
    private final IntegerProperty playerScoreLeft = new SimpleIntegerProperty();
    private final IntegerProperty playerScoreRight= new SimpleIntegerProperty();

    private final StringProperty playerTurn = new SimpleStringProperty();

    private final BooleanProperty canForfeit = new SimpleBooleanProperty(false);

    /**
     * Constructor for GameViewModel.
     *
     * @param initial The initial game state.
     * @param vh      The view handler to manage view transitions.
     * @param isHost  Boolean property indicating if the client is the host.
     */
    public GameViewModel(Game initial, ViewHandler vh, BooleanProperty isHost) {
        this.viewHandler = vh;
        this.model = initial;
        this.isHost = isHost;
        for (int i = 0; i< GameBoard.BOARD_SIZE; i++) {
            seedCounts.add(new SimpleIntegerProperty());
        }
        updateTurnText();
        bind();
    }

    /**
     * Sets the game model and updates the view properties accordingly.
     *
     * @param g The game model to set.
     */
    public void setGame(Game g) {
        this.model = g;
        updateTurnText();
        bind();
        canForfeit.set( model != null && model.canForfeit() );
    }

    /**
     * Updates the game model and view properties.
     */
    public ReadOnlyBooleanProperty isHostProperty() {
        return this.isHost;
    }

    /**
     * Binds the view properties to the game model.
     * This method initializes player names, scores, and seed counts for each pit.
     */
    private void bind() {
        if (model == null) return;
        // initialize names and scores
        playerNameLeft.set(  model.getPlayers()[0].getUsername());
        playerNameRight.set( model.getPlayers()[1].getUsername());
        playerScoreLeft.set(  model.getPlayerScores()[0].getScore());
        playerScoreRight.set( model.getPlayerScores()[1].getScore());
        // seeds per slot
        List<Node> board = model.getGameBoard().getBoard();
        for (int i=0; i<seedCounts.size() && i<board.size(); i++) {
            int seeds = (board.get(i)).getTile().getSeeds();
            seedCounts.get(i).set(seeds);
        }
    }

    /**
     * Updates the view properties based on the current game state.
     * This method is called when the game state changes.
     */
    private void updateTurnText() {
        int turnIndex = model.getCurrentPlayerIndex();
        playerTurn.set(model.getPlayers()[turnIndex].getUsername());
    }

    /**
     * Returns the seed count for a specific pit index.
     *
     * @param idx The index of the pit.
     * @return The read-only integer property representing the seed count.
     */
    public ReadOnlyIntegerProperty seedCountProperty(int idx) {
        return seedCounts.get(idx);
    }

    public ReadOnlyStringProperty  playerNameLeftProperty() {return playerNameLeft;}
    public ReadOnlyStringProperty  playerNameRightProperty(){return playerNameRight;}
    public ReadOnlyIntegerProperty playerScoreLeftProperty(){return playerScoreLeft;}
    public ReadOnlyIntegerProperty playerScoreRightProperty(){return playerScoreRight;}
    public ReadOnlyStringProperty  playerTurnProperty() {return playerTurn;}
    public ReadOnlyBooleanProperty canForfeitProperty() {return canForfeit;}

    /**
     * Handles the click event on a pit.
     * This method sends a request to the server to select the specified pit.
     *
     * @param idx The index of the pit that was clicked.
     */
    public void onPitClicked(int idx) {
        new Thread(() -> {
            try {
                Client.get().select(idx);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Requests the server to forfeit the game.
     * This method is called when the player chooses to forfeit.
     */
    public void askForfeit() {
        new Thread(() -> {
            try {
                Client.get().forfeit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Handles the event when the player wants to return to the main menu.
     * This method sends a request to leave the game and then opens the main menu view.
     */
    public void onBackToMenu() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Client.get().leave();
                Client.reconnect();
                return null;
            }
        };

        task.setOnSucceeded(ev -> {
            try {
                viewHandler.openMainMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        task.setOnFailed(ev -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> {
                try { viewHandler.openMainMenu(); }
                catch (IOException ex) { ex.printStackTrace(); }
            });
        });

        new Thread(task).start();
    }
}
