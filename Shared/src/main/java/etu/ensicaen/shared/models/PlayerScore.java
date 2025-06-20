package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;

public class PlayerScore implements Comparable<PlayerScore>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer score = 0;
    private final Player player;

    public PlayerScore(Player p) {
        this.player = p;
    }

    public void increase() {
        this.score++;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Integer getScore() {
        return this.score;
    }

    @Override
    public int compareTo(PlayerScore ps) {
        return this.getScore().compareTo(ps.getScore());
    }
}
