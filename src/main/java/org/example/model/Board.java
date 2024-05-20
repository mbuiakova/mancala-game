package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.example.exception.ErrorCode;
import org.example.exception.GameLogicException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the mancala board of the game.
 */
public class Board {

    @Getter
    private final int[] pits;
    @Getter
    @Setter
    private int currentPlayer;
    private final int numberOfPitsPerPlayer;
    private final int stonesPerPit;
    /**
     * The number of players in the game.
     */
    public static final int PLAYER_COUNT = 2;
    private static final int DEFAULT_NUMBER_OF_PITS_PER_PLAYER = 6;
    private static final int DEFAULT_STONES_PER_PIT = 6;

    /**
     * Creates a new board with the given number of pits per player and stones per pit.
     *
     * @param numberOfPitsPerPlayer the number of pits per player
     * @param stonesPerPit          the number of stones per pit
     */
    public Board(final int numberOfPitsPerPlayer, final int stonesPerPit) {
        pits = new int[numberOfPitsPerPlayer * PLAYER_COUNT + 2];

        Arrays.fill(pits, stonesPerPit);

        pits[numberOfPitsPerPlayer] = 0; // Player 1's store
        pits[numberOfPitsPerPlayer * PLAYER_COUNT + 1] = 0; // Player 2's store

        currentPlayer = 1;
        this.numberOfPitsPerPlayer = numberOfPitsPerPlayer;
        this.stonesPerPit = stonesPerPit;
    }

    /**
     * Creates a new board with the default number of pits per player and stones per pit.
     *
     * @see #Board(int, int)
     * @see #DEFAULT_NUMBER_OF_PITS_PER_PLAYER
     * @see #DEFAULT_STONES_PER_PIT
     */
    public Board() {
        this(DEFAULT_NUMBER_OF_PITS_PER_PLAYER, DEFAULT_STONES_PER_PIT);
    }

    /**
     * Checks if the pit with the given index exists.
     *
     * @param pitIndex the index of the pit
     * @return true if the pit exists, false otherwise
     */
    public boolean checkPitExists(final int pitIndex) {
        return pitIndex >= 0 && pitIndex < pits.length;
    }

    public boolean checkCorrectPlayersTurn(final int pitIndex) {
        return (currentPlayer == 1 && pitIndex >= 0 && pitIndex < numberOfPitsPerPlayer) || (currentPlayer == 2 && pitIndex > numberOfPitsPerPlayer && pitIndex < pits.length - 1);
    }

    /**
     * Checks if the pit with the given index is empty.
     *
     * @param pitIndex the index of the pit
     * @return true if the pit is empty, false otherwise
     */
    public boolean checkPitIsEmpty(final int pitIndex) {
        return getStonesInPit(pitIndex) == 0;
    }

    /**
     * Gets the number of stones in the pit with the given index.
     *
     * @param pitIndex the index of the pit
     * @return the number of stones in the pit
     */
    public int getStonesInPit(final int pitIndex) {
        return pits[pitIndex];
    }

/**
     * Throws a GameLogicException if the move is invalid.
     *
     * @param pitIndex the index of the pit picked to make a move
     * @return true if the move is invalid, false otherwise
     *
     * @throws GameLogicException if the move is invalid
     */
    public void throwIfInvalidMove(final int pitIndex) {
        if (!checkPitExists(pitIndex)) {
            throw new GameLogicException(ErrorCode.PIT_DOES_NOT_EXIST, getCurrentPlayer(), pitIndex);
        } else if (!checkCorrectPlayersTurn(pitIndex)) {
            throw new GameLogicException(ErrorCode.WRONG_PLAYER_TURN, getCurrentPlayer(), pitIndex);
        } else if (checkPitIsEmpty(pitIndex)) {
            throw new GameLogicException(ErrorCode.EMPTY_PIT, getCurrentPlayer(), pitIndex);
        } else if (isGameOver()) {
            throw new GameLogicException(ErrorCode.GAME_OVER, getCurrentPlayer(), pitIndex);
        }
    }

    /**
     * Moves the stones from the pit with the given index.
     *
     * @param pickedPitIndex the index of the picked pit
     * @return the moves made.
     *
     * @see Move
     */
    public List<Move> moveStones(final int pickedPitIndex) {
        throwIfInvalidMove(pickedPitIndex);

        final List<Move> moves = new ArrayList<>();
        int capturedStonesCount = pits[pickedPitIndex];
        pits[pickedPitIndex] = 0;
        int currentWalkingPitIndex = pickedPitIndex;

        while (capturedStonesCount > 0) {
            currentWalkingPitIndex = (currentWalkingPitIndex + 1) % pits.length;

            pits[currentWalkingPitIndex]++;
            capturedStonesCount--;
            moves.add(new Move(pickedPitIndex, currentWalkingPitIndex));
        }

        changeTurnIfNecessary(currentWalkingPitIndex);
        collectRemainingStonesIfGameOver();

        return moves;
    }

    /**
     * Changes the turn if the last stone was dropped in the player's store.
     *
     * @param stoppedAtPitIndex the index of the pit where the last stone was dropped
     */
    private void changeTurnIfNecessary(final int stoppedAtPitIndex) {
        if (!hasExtraTurn(stoppedAtPitIndex)) {
            changeTurn();
        }
    }

    /**
     * Collects the remaining stones if the game is over.
     */
    private void collectRemainingStonesIfGameOver() {
        if (isGameOver()) {
            collectRemainingStones();
        }
    }

    /**
     * Changes the current player.
     */
    private void changeTurn() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    /**
     * Checks if the player has an extra turn.
     *
     * @param index the index of the pit where the last stone was dropped
     * @return true if the player has an extra turn, false otherwise
     */
    private boolean hasExtraTurn(int index) {
        return (currentPlayer == 1 && index == numberOfPitsPerPlayer) || (currentPlayer == 2 && index == pits.length - 1);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        boolean player1Empty = true;
        boolean player2Empty = true;
        for (int i = 0; i < numberOfPitsPerPlayer; i++) {
            if (pits[i] != 0) {
                player1Empty = false;
            }
        }
        for (int i = numberOfPitsPerPlayer + 1; i < pits.length - 1; i++) {
            if (pits[i] != 0) {
                player2Empty = false;
            }
        }
        return player1Empty || player2Empty;
    }

    /**
     * Collects the remaining stones and puts them in the stores.
     */
    public void collectRemainingStones() {
        for (int i = 0; i < numberOfPitsPerPlayer; i++) {
            pits[numberOfPitsPerPlayer] += pits[i];
            pits[i] = 0;
        }
        for (int i = numberOfPitsPerPlayer + 1; i < pits.length - 1; i++) {
            pits[pits.length - 1] += pits[i];
            pits[i] = 0;
        }
    }

    /**
     * Determines the winner of the game.
     *
     * @return the winner of the game
     */
    public String determineWinner() {
        if (pits[numberOfPitsPerPlayer] > pits[pits.length - 1]) {
            return "Player 1 wins!";
        } else if (pits[pits.length - 1] > pits[numberOfPitsPerPlayer]) {
            return "Player 2 wins!";
        } else {
            return "It's a tie!";
        }
    }
}