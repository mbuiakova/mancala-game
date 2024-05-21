package org.example.model;

/**
 * Represents the winner of the game.
 */
public sealed interface Winner permits Winner.PlayerWinner, Winner.Tie {
    // Nested record for the Player case
    record PlayerWinner(Player player) implements Winner {}

    // Nested record for the Tie case
    record Tie() implements Winner {}
}