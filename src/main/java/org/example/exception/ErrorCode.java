package org.example.exception;

public enum ErrorCode {
    PIT_DOES_NOT_EXIST("Invalid move for player %d in pit %d! The pit does not exist."),
    WRONG_PLAYER_TURN("Invalid move for player %d in pit %d! Only your own pits are allowed to be picked."),
    EMPTY_PIT("Invalid move for player %d in pit %d! The pit is empty."),
    GAME_OVER("Game is over! Please restart the game.");

    private final String messageTemplate;

    ErrorCode(final String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessageTemplate(final int currentPlayer, final int pitIndex) {
        return messageTemplate.formatted(currentPlayer, pitIndex);
    }
}