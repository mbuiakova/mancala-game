package org.example.model;

import jdk.jfr.StackTrace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a move in the game.
 */
@Getter
@StackTrace
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    /**
     * The index of the pit to move the stones from.
     */
    private int fromPitIndex;

    /**
     * The index of the pit to move the stones to.
     */
    private int toPitIndex;
}