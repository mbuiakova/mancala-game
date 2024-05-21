package org.example.exception;

import lombok.Getter;
import org.example.model.Player;

/**
 * Represents an exception that occurs during the game logic processing.
 */
@Getter
public class GameLogicException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Player currentPlayer;
    private final int pitIndex;

    /**
     * Creates a new GameLogicException with the given error code, current player, and pit index.
     *
     * @param errorCode    the error code
     * @param currentPlayer the current player
     * @param pitIndex     the pit index
     */
    public GameLogicException(final ErrorCode errorCode, final Player currentPlayer, final int pitIndex) {
        super(errorCode.getMessageTemplate(currentPlayer, pitIndex));
        this.errorCode = errorCode;
        this.currentPlayer = currentPlayer;
        this.pitIndex = pitIndex;
    }
}
