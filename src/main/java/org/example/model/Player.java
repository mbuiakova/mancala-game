package org.example.model;

import java.io.Serializable;

/**
 * Represents the player in the game.
 */
public enum Player implements Serializable {
    /**
     * Player one.
     */
    ONE,

    /**
     * Player two.
     */
    TWO;

    /**
     * Returns the next player.
     *
     * @return the next player
     */
    public Player nextPlayer() {
        return this == ONE ? TWO : ONE;
    }

    /**
     * Returns true if the player is player one.
     *
     * @return true if the player is player one
     */
    public boolean isPlayerOne() {
        return this == ONE;
    }

    /**
     * Returns true if the player is player two.
     *
     * @return true if the player is player two
     */
    public boolean isPlayerTwo() {
        return this == TWO;
    }

    @Override
    public String toString() {
        return this == ONE ? "Player One" : "Player Two";
    }
}
