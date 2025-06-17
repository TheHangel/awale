package etu.ensicaen.client.viewsmodels;

import etu.ensicaen.client.handlers.ViewHandler;
import etu.ensicaen.client.models.MainMenu;

import java.io.IOException;

public class MainMenuViewModel {
    private final MainMenu model;
    private final ViewHandler viewHandler;

    public MainMenuViewModel(ViewHandler vh) {
        this.model = new MainMenu();
        this.viewHandler = vh;
    }

    public void onPlay() {
        try {
            viewHandler.openGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onHost() {
        this.model.host();
    }

    public void onJoin(String id) {
        this.model.join(id);
    }
}
