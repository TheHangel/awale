package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import etu.ensicaen.shared.models.Leaderboard;
import etu.ensicaen.shared.models.PlayerScore;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Comparator;

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

    @FXML
    public ListView leaderboardListView;

    public void init(MainMenuViewModel vm) {
        this.viewModel = vm;
        this.viewModel.loadLeaderboard();

        this.waitingPlayersText.visibleProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.sessionIdTextFieldResult.visibleProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.playButtonHost.visibleProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.playButtonJoin.visibleProperty().bind(this.viewModel.isJoinedProperty());
        this.sessionIdTextFieldResult.textProperty().bind(this.viewModel.sessionIdProperty());
        this.hostTab.disableProperty().bind(this.viewModel.isJoinedProperty());
        this.joinTab.disableProperty().bind(this.viewModel.isWaitingVisibleProperty());
        this.joinedText.visibleProperty().bind(this.viewModel.isJoinedProperty());

        leaderboardListView.setItems(viewModel.leaderboardProperty());

        leaderboardListView.setCellFactory(lv -> new ListCell<PlayerScore>() {
            @Override
            protected void updateItem(PlayerScore item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPlayer().getUsername()
                            + " — " + item.getScore());
                }
            }
        });

        leaderboardListView.refresh();
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
