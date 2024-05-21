package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.example.exception.ErrorCode;
import org.example.exception.GameLogicException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Represents the mancala board of the game.
 * <p>
 * The board consists of pits and stores. The board is only for two players.
 */
public class Board {
    @Getter
    private final int[] pits;
    @Getter
    @Setter
    private Player currentPlayer;
    @Getter
    private final int numberOfPitsPerPlayer;
    @Getter
    private final int stonesPerPit;
    /**
     * The number of players in the game.
     */
    public static final int PLAYER_COUNT = Player.values().length;
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

        this.currentPlayer = Player.ONE;
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

    /**
     * Checks if it is the correct player's turn.
     *
     * @param pitIndex the index of the pit
     * @return true if it is the correct player's turn, false otherwise
     */
    public boolean checkCorrectPlayersTurn(final int pitIndex) {
        return getPlayersPitsIndicesRange(currentPlayer).anyMatch(i -> i == pitIndex);
    }


    /**
     * Gets the pits indices of the player.
     *
     * @param player the player
     * @return the pits indices of the player
     */
    public IntStream getPlayersPitsIndicesRange(final Player player) {
        final int startIndexInclusive = player.isPlayerOne() ? 0 : numberOfPitsPerPlayer + 1;
        final int endIndexExclusive = player.isPlayerOne() ? numberOfPitsPerPlayer : pits.length - 1;

        return IntStream.range(startIndexInclusive, endIndexExclusive);
    }

    /**
     * Gets the pits of the player.
     *
     * @param player the player
     * @return the pits of the player
     */
    public IntStream getPlayersPits(final Player player) {
        return getPlayersPitsIndicesRange(player).map(i -> pits[i]);
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
     * Gets the total number of stones in the board.
     *
     * @return the total number of stones in the board
     */
    public int getTotalStonesCount() {
        return Arrays.stream(pits).sum();
    }

    /**
     * Throws a GameLogicException if the move is invalid.
     *
     * @param pitIndex the index of the pit picked to make a move
     * @return true if the move is invalid, false otherwise
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
        currentPlayer = currentPlayer.nextPlayer();
    }

    /**
     * Checks if the player has an extra turn.
     *
     * @param stoppedPitIndex the index of the pit where the last stone was dropped
     * @return true if the player has an extra turn, false otherwise
     */
    private boolean hasExtraTurn(final int stoppedPitIndex) {
        final var value = getPlayersPitsIndicesRange(currentPlayer).max();
        return value.isPresent() && value.getAsInt() + 1 == stoppedPitIndex;
    }

    /**
     * Gets the index of the store pit for the player.
     *
     * @param player the player
     * @return the index of the store pit
     */
    public int getStoreIndexForPlayer(final Player player) {
        return player.isPlayerOne() ? numberOfPitsPerPlayer : pits.length - 1;
    }

    /**
     * Gets the number of stones in the store for the player.
     *
     * @param player the player
     * @return the number of stones in the store
     */
    public int getStoredStonesCountForPlayer(final Player player) {
        return pits[getStoreIndexForPlayer(player)];
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        final boolean player1Empty = getPlayersPitsIndicesRange(Player.ONE).allMatch(i -> pits[i] == 0);
        final boolean player2Empty = getPlayersPitsIndicesRange(Player.TWO).allMatch(i -> pits[i] == 0);

        return player1Empty || player2Empty;
    }

    /**
     * Collects the remaining stones and puts them in the stores.
     */
    public void collectRemainingStones() {
        getPlayersPitsIndicesRange(Player.ONE).forEach(i -> {
            pits[getStoreIndexForPlayer(Player.ONE)] += pits[i];
            pits[i] = 0;
        });

        getPlayersPitsIndicesRange(Player.TWO).forEach(i -> {
            pits[getStoreIndexForPlayer(Player.TWO)] += pits[i];
            pits[i] = 0;
        });
    }

    /**
     * Determines the winner of the game.
     *
     * @return the winner of the game
     * @throws GameLogicException if the game is not over
     * @see Winner
     */
    public Winner determineWinner() {
        if (!isGameOver()) {
            throw new GameLogicException(ErrorCode.GAME_NOT_OVER, getCurrentPlayer(), -1);
        }

        final int player1Store = getStoredStonesCountForPlayer(Player.ONE);
        final int player2Store = getStoredStonesCountForPlayer(Player.TWO);

        if (player1Store > player2Store) {
            return new Winner.PlayerWinner(Player.ONE);
        } else if (player2Store > player1Store) {
            return new Winner.PlayerWinner(Player.TWO);
        } else {
            return new Winner.Tie();
        }
    }
}
