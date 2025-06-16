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
        this.viewModel.onHost();
    }

    public void onJoinAction(ActionEvent actionEvent) throws IOException {
        this.viewModel.onJoin(sessionIdTextField.getText().trim());
    }
}
