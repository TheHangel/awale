package etu.ensicaen.shared.models;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A leaderboard system for tracking and ranking player scores.
 */
public class Leaderboard extends TreeSet<PlayerScore> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String FILE_PATH = "leaderboard.dat";

    /**
     * A custom comparator that sorts {@link PlayerScore} objects in descending order of score.
     * If scores are equal, it compares usernames lexicographically to maintain consistency.
     */
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

    /**
     * Constructs an empty leaderboard with the default score comparator.
     */
    public Leaderboard() {
        super(new ScoreComparator());
    }

    /**
     * Adds or replaces the given player score in the leaderboard.
     * If a score for the same player already exists, it will be removed and replaced by the new one.
     *
     * @param ps the new player score
     * @return true if the leaderboard was modified, false otherwise
     */
    @Override
    public boolean add(PlayerScore ps) {
        this.removeIf(existing -> existing.getPlayer().equals(ps.getPlayer()));
        return super.add(ps);
    }

    /**
     * Updates a player's score. Replaces the existing score if it's lower.
     *
     * @param ps the player score to update
     */
    public void updateScore(PlayerScore ps) {
        add(ps);
    }

    /**
     * Returns an iterator over the sorted leaderboard entries.
     *
     * @return an iterator over {@link PlayerScore} entries
     */
    public Iterator<PlayerScore> iteratorSorted() {
        return super.iterator();
    }

    /**
     * Loads the leaderboard from a file.
     * If the file doesn't exist or is corrupted, a new empty leaderboard is returned.
     *
     * @return the loaded leaderboard, or a new one if loading failed
     */
    public static Leaderboard load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Leaderboard) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Fichier inexistant ou corrompu => on retourne un nouveau leaderboard vide
            return new Leaderboard();
        }
    }

    /**
     * Saves the given leaderboard to disk.
     *
     * @param leaderboard the leaderboard to save
     */
    public static void save(Leaderboard leaderboard) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(leaderboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines whether a given score is a new personal best for the player.
     *
     * @param newScore the score to check
     * @return true if the score is higher than the player's current score, or if the player is not on the leaderboard
     */
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