package org.example.model;

import lombok.*;

import java.io.Serializable;

/**
 * Represents a move in the game.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Move implements Serializable {
    /**
     * The index of the pit to move the stones from.
     */
    private int fromPitIndex;

    /**
     * The index of the pit to move the stones to.
     */
    private int toPitIndex;
}