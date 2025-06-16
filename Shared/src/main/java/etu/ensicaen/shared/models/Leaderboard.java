package etu.ensicaen.shared.models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class Leaderboard extends TreeSet<PlayerScore> implements Serializable {
    public Leaderboard() {
        super(Comparator
                .comparing(PlayerScore::getScore).reversed()
                .thenComparing(ps -> ps.getPlayer().getUsername())
        );
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