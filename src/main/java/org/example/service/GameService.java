package org.example.service;

import lombok.Getter;
import org.example.model.Board;
import org.example.model.Move;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Getter
    private Board board;

    public GameService() {
        this.board = new Board();
    }

    public List<Move> makeMove(final int pit) {
        return board.moveStones(pit);
    }

    public boolean isGameOver() {
        return board.isGameOver();
    }

    public void collectRemainingStones() {
        board.collectRemainingStones();
    }

    public String determineWinner() {
        return board.determineWinner();
    }

    public void resetGame() {
        this.board = new Board();
    }
}
