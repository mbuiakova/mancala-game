package org.example.service;

import lombok.Getter;
import org.example.model.Board;
import org.example.model.Move;
import org.example.model.Winner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameService {

    @Getter
    private Board board;
    private final Random random = new Random();

    /**
     * Creates a new game service with a new board.
     */
    public GameService() {
        this.board = new Board();
    }

    /**
     * Gets the random pit index.
     *
     * @return the random pit index
     */
    public int getRandomPitIndex() {
        return random.nextInt(0, board.getPits().length);
    }

    /**
     * Makes a move in the game.
     *
     * @param pit the index of the pit to move the stones from
     * @return the list of moves made
     */
    public List<Move> makeMove(final int pit) {
        return board.moveStones(pit);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return board.isGameOver();
    }

    /**
     * Determines the winner of the game.
     *
     * @return the winner
     */
    public Winner determineWinner() {
        return board.determineWinner();
    }

    /**
     * Gets the winner string.
     *
     * @return the human-readable winner string.
     */
    public String getWinnerString() {
        return switch (determineWinner()) {
            case Winner.PlayerWinner winner -> winner + " wins!";
            case Winner.Tie ignored -> "It's a tie!";
        };
    }

    /**
     * Resets the game.
     */
    public void resetGame() {
        this.board = new Board();
    }
}
