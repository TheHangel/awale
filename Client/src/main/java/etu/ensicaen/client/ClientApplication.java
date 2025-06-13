package etu.ensicaen.client;

import etu.ensicaen.client.handlers.ViewHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class ClientApplication  extends Application {

     @Override
    public void start(Stage stage) throws IOException {
         ViewHandler vh = new ViewHandler(stage);
         vh.openMainMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}