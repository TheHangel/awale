package etu.ensicaen.client.handlers;

import etu.ensicaen.client.views.GameView;
import etu.ensicaen.client.views.MainMenuView;
import etu.ensicaen.client.viewsmodels.GameViewModel;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import etu.ensicaen.shared.models.Game;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Handles switching between different JavaFX views (main menu and game screen).
 * Responsible for loading FXML files and initializing controllers with their respective view models.
 */
public class ViewHandler {
    private final Stage stage;

    /**
     * Constructs a new ViewHandler with the given primary stage.
     *
     * @param stage the JavaFX primary stage to use for view transitions
     */
    public ViewHandler(Stage stage) {
        this.stage = stage;
    }

    /**
     * Loads and displays the main menu view.
     * Initializes its controller with a new {@link MainMenuViewModel}.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void openMainMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/etu/ensicaen/client/main-menu-view.fxml"));
        Parent root = loader.load();
        MainMenuView controller = loader.getController();
        controller.init(new MainMenuViewModel(this));
        stage.setScene(new Scene(root));
        stage.setTitle("Awale – Menu Principal");
        stage.show();
    }

    /**
     * Loads and displays the game view.
     * Initializes its controller with a new {@link GameViewModel} using the provided {@link Game} instance
     * and the host status of the player.
     *
     * @param game    the current game instance to display
     * @param isHost  a boolean property indicating if the player is the host
     * @throws IOException if the FXML file cannot be loaded
     */
    public void openGame(Game game, BooleanProperty isHost) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/etu/ensicaen/client/game-view.fxml"));

        Parent root = loader.load();
        GameView controller = loader.getController();
        controller.init(new GameViewModel(game, this, isHost));

        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.setTitle("Awale – Jeu");
    }
}
