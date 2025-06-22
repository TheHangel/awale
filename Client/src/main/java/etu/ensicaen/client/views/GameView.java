package etu.ensicaen.client.views;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.viewsmodels.GameViewModel;
import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.GameBoard;
import etu.ensicaen.shared.models.Messages;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;

public class GameView {
    private GameViewModel viewModel;

    @FXML private Button tile0, tile1, tile2, tile3,
                          tile4, tile5, tile6, tile7,
                          tile8, tile9, tile10, tile11;
    @FXML private Text playerNameLeftText, playerNameRightText;
    @FXML private Text playerScoreLeftText, playerScoreRightText;

    @FXML
    private Text playerTurnText;

    @FXML
    private Button forfeitButton;

    public void init(GameViewModel viewModel) {
        this.viewModel = viewModel;
        // bindings
        playerNameLeftText.textProperty().bind(
                viewModel.isHostProperty().get()
                        ? viewModel.playerNameLeftProperty()
                        : viewModel.playerNameRightProperty()
        );
        playerNameRightText.textProperty().bind(
                viewModel.isHostProperty().get()
                        ? viewModel.playerNameRightProperty()
                        : viewModel.playerNameLeftProperty()
        );
        playerScoreLeftText.textProperty().bind(
                viewModel.isHostProperty().get()
                        ? viewModel.playerScoreLeftProperty().asString()
                        : viewModel.playerScoreRightProperty().asString()
        );
        playerScoreRightText.textProperty().bind(
                viewModel.isHostProperty().get()
                        ? viewModel.playerScoreRightProperty().asString()
                        : viewModel.playerScoreLeftProperty().asString()
        );

        playerTurnText.textProperty().bind(viewModel.playerTurnProperty());

        forfeitButton.visibleProperty()
                .bind(viewModel.canForfeitProperty());

        Button[] tiles = { tile0, tile1, tile2, tile3,
                           tile4, tile5, tile6, tile7,
                           tile8, tile9, tile10, tile11 };

        boolean isHost = viewModel.isHostProperty().get();
        for (int visual = 0; visual < tiles.length; visual++) {
            final int vis = visual;
            final int logical;
            if (vis < (GameBoard.BOARD_SIZE / 2)) {
                // bottom row clicks
                logical = isHost ? vis : vis + (GameBoard.BOARD_SIZE / 2) % GameBoard.BOARD_SIZE;
            } else {
                // top row clicks
                logical = isHost ? vis : vis - (GameBoard.BOARD_SIZE / 2) % GameBoard.BOARD_SIZE;
            }

            tiles[vis].textProperty().bind(viewModel.seedCountProperty(logical).asString());

            tiles[vis].setOnAction(e -> {
                if (vis < (GameBoard.BOARD_SIZE / 2)) {
                    viewModel.onPitClicked(logical);
                }
            });
        }

        // Listen to changes inside the game
        new Thread(() -> {
            try {
                Object o;
                while ((o = Client.get().readObject()) != null) {
                    if (o instanceof Game g) {
                        Platform.runLater(() -> {
                            this.viewModel.setGame(g);
                        });
                    }
                    else if (o instanceof String message) {
                        Platform.runLater(() -> {
                            if(message.equals("ASK_FORFEIT")) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Forfeit / Forfait");
                                alert.setHeaderText("Your opponent wants to forfeit / Votre adversaire demande forfait");
                                alert.setContentText("");
                                // ask the user if they want to forfeit
                                alert.getButtonTypes().setAll(
                                        new javafx.scene.control.ButtonType("Yes / Oui"),
                                        new javafx.scene.control.ButtonType("No / Non")
                                );
                                alert.showAndWait().ifPresent(response -> {
                                    if (response.getText().equals("Yes / Oui")) {
                                        try {
                                            Client.get().respondForfeit();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                return;
                            }
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            if(message.equals(Messages.ILLEGAL_MESSAGE)) {
                                alert.setTitle(Messages.ILLEGAL_MOVE);
                            }
                            else {
                                alert.setTitle(Messages.OVER_MESSAGE);
                                alert.setOnCloseRequest(event -> {
                                    viewModel.onBackToMenu();
                                });
                            }
                            alert.setHeaderText(message);
                            alert.setContentText("");

                            alert.showAndWait();
                        });
                    }
                }
            }
            catch (Exception ignored) {}
        }).start();
    }

    @FXML
    private void onBackAction() throws IOException {
        viewModel.onBackToMenu();
    }

    @FXML
    private void onForfeitAction() {
        this.viewModel.askForfeit();
    }
}
