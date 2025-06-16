package etu.ensicaen.client.views;

import etu.ensicaen.client.ClientApplication;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MainMenuView {
    private MainMenuViewModel viewModel;

    @FXML
    public TextField sessionIdTextField;

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
                return ClientApplication.client.host();
            }
        };

        task.setOnSucceeded(ev ->
                System.out.println(task.getValue())
        );

        new Thread(task).start();
    }

    public void onJoinAction(ActionEvent actionEvent) throws IOException {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ClientApplication.client.join(sessionIdTextField.getText().trim());
            }
        };

        task.setOnSucceeded(ev ->
                System.out.println(task.getValue())
        );

        task.setOnFailed(ev ->
                System.out.println(task.getValue())
        );

        new Thread(task).start();
    }
}
