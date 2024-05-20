package org.example.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an exception that occurs during the game logic processing.
 */
@Getter
public class GameLogicException extends RuntimeException {
    private final ErrorCode errorCode;
    private final int currentPlayer;
    private final int pitIndex;

    /**
     * Creates a new GameLogicException with the given error code, current player, and pit index.
     *
     * @param errorCode    the error code
     * @param currentPlayer the current player
     * @param pitIndex     the pit index
     */
    public GameLogicException(final ErrorCode errorCode, final int currentPlayer, final int pitIndex) {
        super(errorCode.getMessageTemplate(currentPlayer, pitIndex));
        this.errorCode = errorCode;
        this.currentPlayer = currentPlayer;
        this.pitIndex = pitIndex;
    }
}
