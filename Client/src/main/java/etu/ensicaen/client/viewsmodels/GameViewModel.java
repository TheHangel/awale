package etu.ensicaen.client.viewsmodels;

import etu.ensicaen.client.handlers.ViewHandler;
import etu.ensicaen.shared.models.Game;

import java.io.IOException;

public class GameViewModel {
    private final Game model;
    private final ViewHandler viewHandler;

    public GameViewModel(Game model, ViewHandler vh) {
        this.model = model;
        this.viewHandler = vh;
    }

    public void onPitClicked(int index) {

    }

    public void onBackToMenu() {
        try {
            viewHandler.openMainMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
