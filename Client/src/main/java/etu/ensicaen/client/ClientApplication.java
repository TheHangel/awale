package etu.ensicaen.client;

import etu.ensicaen.client.handlers.ViewHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    public static Client client;

    /*static {
        try {
            client = new Client("localhost", 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public void start(Stage stage) throws IOException {
        ViewHandler vh = new ViewHandler(stage);
        vh.openMainMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}