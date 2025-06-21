package etu.ensicaen.shared.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class Leaderboard extends TreeSet<PlayerScore> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static class ScoreComparator implements Comparator<PlayerScore>, Serializable {
        @Serial private static final long serialVersionUID = 1L;

        @Override
        public int compare(PlayerScore a, PlayerScore b) {
            int cmp = Integer.compare(b.getScore(), a.getScore());
            return (cmp != 0)
                    ? cmp
                    : a.getPlayer().getUsername()
                    .compareTo(b.getPlayer().getUsername());
        }
    }

    public Leaderboard() {
        super(new ScoreComparator());
    }

    @Override
    public boolean add(PlayerScore ps) {
        this.removeIf(existing -> existing.getPlayer().equals(ps.getPlayer()));
        return super.add(ps);
    }

    public void addOrUpdate(PlayerScore ps) {
        add(ps);
    }

    public Iterator<PlayerScore> iteratorSorted() {
        return super.iterator();
    }
}