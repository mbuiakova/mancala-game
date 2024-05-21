package org.example.model;

import org.example.exception.GameLogicException;
import org.example.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testInitialSetup() {
        int[] pits = board.getPits();
;
        board.getPlayersPitsIndicesRange(Player.ONE).forEach(i -> assertEquals(board.getPits()[i], board.getNumberOfPitsPerPlayer()));
        board.getPlayersPitsIndicesRange(Player.TWO).forEach(i -> assertEquals(board.getPits()[i], board.getNumberOfPitsPerPlayer()));

        final int playerOneStoredStonesCount = board.getStoredStonesCountForPlayer(Player.ONE);
        final int playerTwoStoredStonesCount = board.getStoredStonesCountForPlayer(Player.TWO);

        assertEquals(playerOneStoredStonesCount, pits[6]);
        assertEquals(playerTwoStoredStonesCount, pits[13]);
        assertEquals(0, playerOneStoredStonesCount);
        assertEquals(0, playerTwoStoredStonesCount);

        assertEquals(Player.ONE, board.getCurrentPlayer());
    }

    @Test
    void testInvalidMovePitDoesNotExist() {
        GameLogicException exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(-1));
        assertEquals(ErrorCode.PIT_DOES_NOT_EXIST, exception.getErrorCode());

        exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(14));
        assertEquals(ErrorCode.PIT_DOES_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    void testInvalidMoveNotPlayersTurn() {
        board.setCurrentPlayer(Player.TWO);

        final GameLogicException exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(0));

        assertEquals(ErrorCode.WRONG_PLAYER_TURN, exception.getErrorCode());
    }

    @Test
    void testInvalidMoveEmptyPit() {
        final int[] pits = board.getPits();
        pits[0] = 0;

        final GameLogicException exception = assertThrows(GameLogicException.class, () -> board.throwIfInvalidMove(0));

        assertEquals(ErrorCode.EMPTY_PIT, exception.getErrorCode());
    }

    @Test
    void testMoveStones() {
        int[] initialPits = board.getPits().clone();
        initialPits[0] = 6;

        final List<Move> expectedMoves = List.of(new Move(0, 1), new Move(0, 2), new Move(0, 3), new Move(0, 4), new Move(0, 5), new Move(0, 6));
        final List<Move> actualMoves = board.moveStones(0);
        assertEquals(expectedMoves, actualMoves);

        int[] expectedPits = initialPits.clone();
        expectedPits[0] = 0;

        for (int i = 1; i <= 6; i++) {
            expectedPits[i]++;
        }

        assertArrayEquals(expectedPits, board.getPits());
        assertEquals(Player.ONE, board.getCurrentPlayer());
    }

    @Test
    void testExtraTurn() {
        int[] pits = board.getPits();
        pits[5] = 1; // Set last pit before store to 1

        board.moveStones(5);

        assertEquals(Player.ONE, board.getCurrentPlayer(), "Player 1 should get an extra turn");
    }

    @Test
    void testChangeTurn() {
        int[] pits = board.getPits();
        pits[4] = 1; // Set pit to 1 so last stone doesn't land in store

        board.moveStones(4);

        assertEquals(Player.TWO, board.getCurrentPlayer(), "Turn should change to player 2");
    }

    @Test
    void testCaptureStones() {
        int[] pits = board.getPits();
        pits[2] = 4; // Set pit to 1
        pits[11] = 4; // Set opponent pit

        board.moveStones(2);

        assertEquals(0, pits[2]);
        assertEquals(4, pits[11]);
        assertEquals(1, pits[6], "Player 1's store should have captured stones");
    }

    @Test
    void testGameOver() {
        board.getPlayersPitsIndicesRange(Player.ONE).forEach(i -> board.getPits()[i] = 0);

        assertTrue(board.isGameOver(), "Game should be over when one side is empty");
    }

    @Test
    void testCollectRemainingStones() {
        board.collectRemainingStones();

        final int player1Store = board.getStoredStonesCountForPlayer(Player.ONE);
        final int player2Store = board.getStoredStonesCountForPlayer(Player.TWO);

        assertEquals(board.getTotalStonesCount(), player1Store + player2Store, "All stones should be collected in the stores");
    }

    @Test
    void testDetermineWinner() {
        // Ensure the game is over by emptying one side
        board.getPlayersPitsIndicesRange(Player.ONE).forEach(i -> board.getPits()[i] = 0);

        final int playerOneStoreIndex = board.getStoreIndexForPlayer(Player.ONE);
        final int playerTwoStoreIndex = board.getStoreIndexForPlayer(Player.TWO);

        // Check that the first player wins
        board.getPits()[playerOneStoreIndex] = 25;
        board.getPits()[playerTwoStoreIndex] = 20;
        assertEquals(new Winner.PlayerWinner(Player.ONE), board.determineWinner());

        // Check that the second player wins
        board.getPits()[playerOneStoreIndex] = 20;
        board.getPits()[playerTwoStoreIndex] = 25;
        assertEquals(new Winner.PlayerWinner(Player.TWO), board.determineWinner());

        // Update the store counts for a tie
        board.getPits()[playerOneStoreIndex] = 22;
        board.getPits()[playerTwoStoreIndex] = 22;
        assertEquals(new Winner.Tie(), board.determineWinner());
    }

    @Test
    void testDetermineWinnerThrowsExceptionIfGameNotOver() {
        // Ensure the game is not over by having non-empty pits on both sides
        board.getPits()[0] = 6;
        board.getPits()[7] = 6;

        // Set the store counts
        board.getPits()[6] = 25;  // Player One's store
        board.getPits()[13] = 20; // Player Two's store

        // Verify exception is thrown
        GameLogicException exception = assertThrows(GameLogicException.class, () -> board.determineWinner());
        assertEquals(ErrorCode.GAME_NOT_OVER, exception.getErrorCode());
    }

    @Test
    void testValidMovePitDoesNotThrow() {
        // The first pits of the both the players should always be valid.
        board.setCurrentPlayer(Player.ONE);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(0));

        board.setCurrentPlayer(Player.TWO);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(board.getNumberOfPitsPerPlayer() + 1));

        // The last pits of the both the players should always be valid.
        board.setCurrentPlayer(Player.ONE);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(board.getNumberOfPitsPerPlayer() - 1));

        board.setCurrentPlayer(Player.TWO);
        assertDoesNotThrow(() -> board.throwIfInvalidMove(board.getPits().length - Board.PLAYER_COUNT));
    }
}
