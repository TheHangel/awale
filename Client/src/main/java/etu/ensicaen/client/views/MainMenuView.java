package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class MainMenuView {
    private MainMenuViewModel viewModel;

    @FXML
    public Text waitingPlayersText, joinedText;

    @FXML
    public TextField sessionIdTextField, sessionIdTextFieldResult;

    @FXML
    public Button playButtonJoin, playButtonHost;

    @FXML
    public Tab joinTab, hostTab;

    public void init(MainMenuViewModel vm) {
        this.viewModel = vm;
        this.waitingPlayersText.visibleProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.sessionIdTextFieldResult.visibleProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.playButtonHost.visibleProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.playButtonJoin.visibleProperty().bind(this.viewModel.isJoinedProperty());
        this.sessionIdTextFieldResult.textProperty().bind(this.viewModel.sessionIdProperty());
        this.hostTab.disableProperty().bind(this.viewModel.isJoinedProperty());
        this.joinTab.disableProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.joinedText.visibleProperty().bind(this.viewModel.isJoinedProperty());
    }

    @FXML
    private void onPlayAction(ActionEvent actionEvent) {
        this.viewModel.onPlay();
    }

    @FXML
    private void onHostAction(ActionEvent actionEvent) {
        this.viewModel.onHost();
    }

    @FXML
    private void onJoinAction(ActionEvent actionEvent) {
        this.viewModel.onJoin(sessionIdTextField.getText().trim());
    }

    @FXML
    private void onQuitAction() throws IOException {
        Client.get().close();
        Platform.exit();
        System.exit(0);
    }
}
