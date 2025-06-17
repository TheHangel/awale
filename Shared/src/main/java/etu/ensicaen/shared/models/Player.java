package etu.ensicaen.shared.models;

import java.io.Serializable;

public class Player implements Serializable {
    private final String username;

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}