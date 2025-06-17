package etu.ensicaen.client;

import etu.ensicaen.client.handlers.ViewHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Client.get("localhost", 12345);
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

    public static void main(String[] args) {
        launch();
    }
}