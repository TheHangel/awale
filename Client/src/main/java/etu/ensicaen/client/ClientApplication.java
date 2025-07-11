package etu.ensicaen.client;

import etu.ensicaen.client.handlers.ViewHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the Awale client.
 * Initializes the client and sets up the main menu view.
 */
public class ClientApplication extends Application {

    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage for this application, onto which the application scene can be set.
     * @throws IOException If an I/O error occurs when loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Client.get(Config.host(), Config.port());
        }
        catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error / Erreur de connexion");
            alert.setHeaderText("Could not connect to the server / Impossible de se connecter au serveur");
            alert.setContentText("Please check your connection settings and try again. / Vérifiez vos paramètres de connexion et réessayez.");
            alert.showAndWait();
            return;
        }

        Font.loadFont(getClass().getResourceAsStream("etu/ensicaen/client/assets/Game Bubble.ttf"), 10);
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main-menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 450);

        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("assets/logo.png")));
        stage.setResizable(false);
        stage.setTitle("Awale Client");
        stage.setOnCloseRequest(ev -> {
            try {
                Client.get().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ViewHandler vh = new ViewHandler(stage);
        vh.openMainMenu();
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        launch();
    }
}