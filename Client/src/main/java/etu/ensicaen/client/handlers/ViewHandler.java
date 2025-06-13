package etu.ensicaen.client.handlers;

import etu.ensicaen.client.views.GameView;
import etu.ensicaen.client.views.MainMenuView;
import etu.ensicaen.client.viewsmodels.GameViewModel;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import etu.ensicaen.shared.models.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewHandler {
    private final Stage stage;

    public ViewHandler(Stage stage) {
        this.stage = stage;
    }

    public void openMainMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/etu/ensicaen/client/main-menu-view.fxml"));

        Parent root = loader.load();
        MainMenuView controller = loader.getController();
        controller.init(new MainMenuViewModel(this));
        stage.setScene(new Scene(root));
        stage.setTitle("Awale – Menu Principal");
        stage.show();
    }

    public void openGame() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/etu/ensicaen/client/game-view.fxml"));

        Parent root = loader.load();
        GameView controller = loader.getController();
        controller.init(new GameViewModel(new Game(), this));
        stage.setScene(new Scene(root));
        stage.setTitle("Awale – Jeu");
    }
}
