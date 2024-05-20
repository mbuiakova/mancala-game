package org.example.model;

import org.example.exception.GameLogicException;
import org.example.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;
    private int numberOfPitsPerPlayer;
    private int stonesPerPit;

    @BeforeEach
    void setUp() {
        numberOfPitsPerPlayer = (int) (Math.random() * 10 + 1);
        stonesPerPit = (int) (Math.random() * 10 + 1);

        board = new Board(numberOfPitsPerPlayer, stonesPerPit);
    }

    @Test
    void testInitialSetup() {
        final int[] pits = board.getPits();

        for (int i = 0; i < numberOfPitsPerPlayer; i++) {
            assertEquals(stonesPerPit, pits[i]);
            assertEquals(stonesPerPit, pits[i + numberOfPitsPerPlayer + 1]);
        }

        assertEquals(0, pits[numberOfPitsPerPlayer]);  // Player 1's store
        assertEquals(0, pits[pits.length - 1]);  // Player 2's store
        assertEquals(1, board.getCurrentPlayer());
    }

    @Test
    void testInvalidMovePitDoesNotExist() {
        GameLogicException exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(-1));
        assertEquals(ErrorCode.PIT_DOES_NOT_EXIST, exception.getErrorCode());

        exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(board.getPits().length));
        assertEquals(ErrorCode.PIT_DOES_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    void testValidMovePitDoesNotThrow() {
        // The first pits of the both the players should always be valid.
        board.setCurrentPlayer(1);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(0));

        board.setCurrentPlayer(2);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(numberOfPitsPerPlayer + 1));

        // The last pits of the both the players should always be valid.
        board.setCurrentPlayer(1);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(numberOfPitsPerPlayer - 1));

        board.setCurrentPlayer(2);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(board.getPits().length - Board.PLAYER_COUNT));
    }

    @Test
    void testInvalidMoveNotPlayersTurn() {
        board.setCurrentPlayer(2);
        GameLogicException exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(0));
        assertEquals(ErrorCode.WRONG_PLAYER_TURN, exception.getErrorCode());
    }

    @Test
    void testInvalidMoveEmptyPit() {
        final int[] pits = board.getPits();
        pits[0] = 0;
        GameLogicException exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(0));
        assertEquals(ErrorCode.EMPTY_PIT, exception.getErrorCode());
    }

    @Test
    void testMoveStones() {
        final int pickedPitIndex = 0;
        final int initialStones = board.getPits()[pickedPitIndex];
        final List<Move> moves = board.moveStones(pickedPitIndex);

        assertEquals(initialStones, moves.size());
        assertEquals(0, board.getPits()[pickedPitIndex]);

        int currentPitIndex = pickedPitIndex;
        for (int i = 0; i < initialStones; i++) {
            currentPitIndex = (currentPitIndex + 1) % board.getPits().length;
            if (currentPitIndex == numberOfPitsPerPlayer && board.getCurrentPlayer() == 2) {
                // Skip Player 1's store when it's Player 2's turn
                currentPitIndex = (currentPitIndex + 1) % board.getPits().length;
            } else if (currentPitIndex == board.getPits().length - 1 && board.getCurrentPlayer() == 1) {
                // Skip Player 2's store when it's Player 1's turn
                currentPitIndex = 0;
            }
            assertEquals(stonesPerPit + 1, board.getPits()[currentPitIndex]);
        }
    }

    @Test
    void testExtraTurn() {
        board.getPits()[numberOfPitsPerPlayer - 1] = 1;  // Set last pit before store to 1
        board.moveStones(numberOfPitsPerPlayer - 1);
        assertEquals(1, board.getCurrentPlayer(), "Player 1 should get an extra turn");
    }

    @Test
    void testChangeTurn() {
        board.getPits()[numberOfPitsPerPlayer - 2] = 1;  // Set pit to 1 so last stone doesn't land in store
        board.moveStones(numberOfPitsPerPlayer - 2);
        assertEquals(2, board.getCurrentPlayer(), "Turn should change to player 2");
    }

    @Test
    void testCaptureStones() {
        board.setCurrentPlayer(1);
        final int capturePitIndex = numberOfPitsPerPlayer - 1;
        // Make sure the last stone lands in the player's store.
        board.getPits()[capturePitIndex] = 1;
        board.getPits()[numberOfPitsPerPlayer] = 5;
        board.moveStones(capturePitIndex);

        // Check that the stone has moved away from the picked position.
        assertEquals(0, board.getPits()[capturePitIndex]);
        // Make sure it didn't get to the Player 2's store.
        assertEquals(0, board.getPits()[board.getPits().length - 1]);
        // Check that the stone ended up in the Player 1's store.
        assertEquals(6, board.getPits()[numberOfPitsPerPlayer], "Player 1's store should have captured stones");
    }

    @Test
    void testGameOver() {
        for (int i = 0; i < numberOfPitsPerPlayer; i++) {
            board.getPits()[i] = 0;
        }

        assertTrue(board.isGameOver(), "Game should be over when one side is empty");
    }

    @Test
    void testCollectRemainingStones() {
        board.collectRemainingStones();

        final int player1Store = board.getPits()[numberOfPitsPerPlayer];
        final int player2Store = board.getPits()[board.getPits().length - 1];

        assertEquals((numberOfPitsPerPlayer * stonesPerPit * 2) - player1Store - player2Store, 0, "All stones should be collected in the stores");
    }

    @Test
    void testDetermineWinner() {
        board.getPits()[numberOfPitsPerPlayer] = 25;
        board.getPits()[board.getPits().length - 1] = 20;
        assertEquals("Player 1 wins!", board.determineWinner());

        board.getPits()[numberOfPitsPerPlayer] = 20;
        board.getPits()[board.getPits().length - 1] = 25;
        assertEquals("Player 2 wins!", board.determineWinner());

        board.getPits()[numberOfPitsPerPlayer] = 22;
        board.getPits()[board.getPits().length - 1] = 22;
        assertEquals("It's a tie!", board.determineWinner());
    }
}
