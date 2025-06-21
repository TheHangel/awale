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

    private final StringProperty playerTurn = new SimpleStringProperty();

    private final BooleanProperty canForfeit = new SimpleBooleanProperty(false);

    public GameViewModel(Game initial, ViewHandler vh, BooleanProperty isHost) {
        this.viewHandler = vh;
        this.model = initial;
        this.isHost = isHost;
        for (int i = 0; i< GameBoard.BOARD_SIZE; i++) {
            seedCounts.add(new SimpleIntegerProperty());
        }
        updateTurnText();
        bind();
    }

    public void setGame(Game g) {
        this.model = g;
        updateTurnText();
        bind();
        canForfeit.set( model != null && model.canForfeit() );
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

    private void updateTurnText() {
        int turnIndex = model.getCurrentPlayerIndex();
        playerTurn.set(model.getPlayers()[turnIndex].getUsername());
    }

    public ReadOnlyIntegerProperty seedCountProperty(int idx) {
        return seedCounts.get(idx);
    }

    public ReadOnlyStringProperty  playerNameLeftProperty() {return playerNameLeft;}
    public ReadOnlyStringProperty  playerNameRightProperty(){return playerNameRight;}
    public ReadOnlyIntegerProperty playerScoreLeftProperty(){return playerScoreLeft;}
    public ReadOnlyIntegerProperty playerScoreRightProperty(){return playerScoreRight;}
    public ReadOnlyStringProperty  playerTurnProperty() {return playerTurn;}
    public ReadOnlyBooleanProperty canForfeitProperty() {return canForfeit;}

    public void onPitClicked(int idx) {
        new Thread(() -> {
            try {
                Client.get().select(idx);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void askForfeit() {
        new Thread(() -> {
            try {
                Client.get().forfeit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void onBackToMenu() throws IOException {
        new Thread(() -> {
            try {
                Client.get().leave();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
        viewHandler.openMainMenu();
    }
}
