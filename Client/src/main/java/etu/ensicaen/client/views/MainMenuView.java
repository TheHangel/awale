package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.ClientApplication;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainMenuView {
    private MainMenuViewModel viewModel;

    public void init(MainMenuViewModel vm) {
        this.viewModel = vm;
    }

    @FXML
    private void onPlayAction() throws IOException {
        //viewModel.onPlay();
        ClientApplication.client = new Client("localhost", 12345);
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ClientApplication.client.sendMessage("play");
            }
        };
        task.setOnSucceeded(ev -> System.out.println(task.getValue()));
        //task.setOnFailed(ev -> ev);

        new Thread(task).start();
    }
}
