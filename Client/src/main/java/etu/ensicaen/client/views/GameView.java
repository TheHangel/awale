package etu.ensicaen.client.views;

import etu.ensicaen.client.viewsmodels.GameViewModel;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class GameView {
    @FXML
    private GridPane boardGrid;
    private GameViewModel viewModel;

    public void init(GameViewModel vm) {
        this.viewModel = vm;
        bindBoard();
    }

    private void bindBoard() {
        boardGrid.getChildren().clear();
        // bind pour chaque truc par exemple
        /*for (int i = 0; i < viewModel.getPits().size(); i++) {
            Button pitBtn = new Button();
            pitBtn.textProperty().bind(viewModel.getPits().get(i).asString());
            final int idx = i;
            pitBtn.setOnAction(e -> viewModel.onPitClicked(idx));
            boardGrid.add(pitBtn, i % 6, i / 6);
        }*/
    }

    @FXML
    private void onBackAction() {
        viewModel.onBackToMenu();
    }
}
