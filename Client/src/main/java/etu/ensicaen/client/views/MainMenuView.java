package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.ClientApplication;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenuView {
    private MainMenuViewModel viewModel;

    public void init(MainMenuViewModel vm) {
        this.viewModel = vm;
    }

    @FXML
    private void onPlayAction() throws IOException {
        ClientApplication.client = new Client("localhost", 12345);
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ClientApplication.client.sendMessage("PLAY");
            }
        };
        task.setOnSucceeded(ev ->
                viewModel.onPlay()
        );

        new Thread(task).start();
    }
}
