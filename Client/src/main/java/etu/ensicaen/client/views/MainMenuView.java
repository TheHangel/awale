package etu.ensicaen.client.views;

import etu.ensicaen.client.viewsmodels.MainMenuViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuView {
    private MainMenuViewModel viewModel;

    @FXML
    private Button playButton;

    public void init(MainMenuViewModel vm) {
        this.viewModel = vm;
    }

    @FXML
    private void onPlayAction() {
        viewModel.onPlay();
    }
}
