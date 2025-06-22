package etu.ensicaen.shared.models;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class Leaderboard extends TreeSet<PlayerScore> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String FILE_PATH = "leaderboard.dat";

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

    public void updateScore(PlayerScore ps) {
        add(ps);
    }

    public Iterator<PlayerScore> iteratorSorted() {
        return super.iterator();
    }

    public static Leaderboard load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Leaderboard) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Fichier inexistant ou corrompu => on retourne un nouveau leaderboard vide
            return new Leaderboard();
        }
    }

    public static void save(Leaderboard leaderboard) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(leaderboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isNewHighScore(PlayerScore newScore) {
        for (PlayerScore existing : this) {
            if (existing.getPlayer().equals(newScore.getPlayer())) {
                return newScore.getScore() > existing.getScore();
            }
        }
        //if player isn't in lb, it's a new personal best for them
        return true;
    }


}