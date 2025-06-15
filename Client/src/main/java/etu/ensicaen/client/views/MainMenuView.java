package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.ClientApplication;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenuView {
    private MainMenuViewModel viewModel;

    public void init(MainMenuViewModel vm) {
        this.viewModel = vm;
    }

    @FXML
    private void onPlayAction(ActionEvent actionEvent) throws IOException {
    }

    public void onHostAction(ActionEvent actionEvent) throws IOException {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ClientApplication.client.sendMessage("HOST");
            }
        };

        task.setOnSucceeded(ev ->
                System.out.println(task.getValue())
        );

        new Thread(task).start();
    }

    public void onJoinAction(ActionEvent actionEvent) throws IOException {
        /*String response = ClientApplication.client.sendMessage("JOIN:");
        System.out.println(response);*/
    }
}
