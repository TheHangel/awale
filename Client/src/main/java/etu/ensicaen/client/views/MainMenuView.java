package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import etu.ensicaen.shared.models.PlayerScore;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controller class for the Main Menu view in the JavaFX application.
 * Handles interactions with the UI components and binds them to the {@link MainMenuViewModel}.
 * Manages user input for hosting or joining a session and displays the leaderboard.
 */
public class MainMenuView {
    private MainMenuViewModel viewModel;

    @FXML
    public Text waitingPlayersText, joinedText;

    @FXML
    public TextField sessionIdTextField, sessionIdTextFieldResult;

    @FXML
    public TextField usernameTextField;

    @FXML
    public Button playButtonJoin, playButtonHost;

    @FXML
    public Tab joinTab, hostTab;

    @FXML
    public ListView leaderboardListView;

    /**
     * Initializes the view with the provided view model, sets up bindings, and loads leaderboard data.
     *
     * @param vm the view model to bind the UI components to
     */
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

        this.usernameTextField.setText("Player");
        this.viewModel.usernameProperty().bind(this.usernameTextField.textProperty());

        this.usernameTextField.disableProperty().bind(
                this.viewModel.isJoinedProperty()
                .or(this.viewModel.isWaitingVisibleProperty())
        );

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

    /**
     * Called when the "Play" button is clicked. Starts the game.
     *
     * @param actionEvent the event triggered by the button
     */
    @FXML
    private void onPlayAction(ActionEvent actionEvent) {
        this.viewModel.onPlay();
    }

    /**
     * Called when the "Host" button is clicked. Hosts a new game session.
     *
     * @param actionEvent the event triggered by the button
     */
    @FXML
    private void onHostAction(ActionEvent actionEvent) {
        this.viewModel.onHost();
    }

    /**
     * Called when the "Join" button is clicked. Attempts to join a session using the provided ID.
     *
     * @param actionEvent the event triggered by the button
     */
    @FXML
    private void onJoinAction(ActionEvent actionEvent) {
        this.viewModel.onJoin(sessionIdTextField.getText().trim());
    }

    /**
     * Called when the "Quit" button is clicked. Closes the client and exits the application.
     *
     * @throws IOException if an error occurs while closing the client
     */
    @FXML
    private void onQuitAction() throws IOException {
        Client.get().close();
        Platform.exit();
        System.exit(0);
    }
}
