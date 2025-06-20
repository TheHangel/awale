package etu.ensicaen.client.viewsmodels;

import etu.ensicaen.client.Client;
import etu.ensicaen.client.handlers.ViewHandler;
import etu.ensicaen.shared.models.Game;
import etu.ensicaen.shared.models.GameBoard;
import etu.ensicaen.shared.models.Node;
import javafx.beans.property.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameViewModel {
    private Game model;
    private final ViewHandler viewHandler;

    private final BooleanProperty isHost;
    private final List<IntegerProperty> seedCounts = new ArrayList<>();
    private final StringProperty playerNameLeft   = new SimpleStringProperty();
    private final StringProperty playerNameRight  = new SimpleStringProperty();
    private final IntegerProperty playerScoreLeft = new SimpleIntegerProperty();
    private final IntegerProperty playerScoreRight= new SimpleIntegerProperty();

    public GameViewModel(Game initial, ViewHandler vh, BooleanProperty isHost) {
        this.viewHandler = vh;
        this.model = initial;
        this.isHost = isHost;
        for (int i = 0; i< GameBoard.BOARD_SIZE; i++) {
            seedCounts.add(new SimpleIntegerProperty());
        }
        bind();
    }

    public void setGame(Game g) {
        this.model = g;
        bind();
    }

    public ReadOnlyBooleanProperty isHostProperty() {
        return this.isHost;
    }

    private void bind() {
        if (model == null) return;
        // initialize names and scores
        playerNameLeft.set(  model.getPlayers()[0].getUsername());
        playerNameRight.set( model.getPlayers()[1].getUsername());
        playerScoreLeft.set(  model.getPlayerScores()[0].getScore());
        playerScoreRight.set( model.getPlayerScores()[1].getScore());
        // seeds per slot
        List<Node> board = model.getGameBoard().getBoard();
        for (int i=0; i<seedCounts.size() && i<board.size(); i++) {
            int seeds = (board.get(i)).getTile().getSeeds();
            seedCounts.get(i).set(seeds);
        }
    }

    public ReadOnlyIntegerProperty seedCountProperty(int idx) {
        return seedCounts.get(idx);
    }

    public ReadOnlyStringProperty  playerNameLeftProperty() {return playerNameLeft;}
    public ReadOnlyStringProperty  playerNameRightProperty(){return playerNameRight;}
    public ReadOnlyIntegerProperty playerScoreLeftProperty(){return playerScoreLeft;}
    public ReadOnlyIntegerProperty playerScoreRightProperty(){return playerScoreRight;}

    public void onPitClicked(int idx) {
        new Thread(() -> {
            try {
                Client.get().select(idx);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void onBackToMenu() throws IOException {
        viewHandler.openMainMenu();
    }
}
