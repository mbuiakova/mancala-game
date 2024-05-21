package org.example.controller;

import org.example.exception.GameLogicException;
import org.example.model.Move;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.example.model.Board;
import org.example.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The Spring controller for the game.
 * It handles the HTTP requests and responses.
 */
@Controller
public class GameController {

    private final GameService gameService;

    /**
     * Creates a new GameController with the given GameService.
     *
     * @param gameService the game service
     */
    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles the index page.
     *
     * @param model the model
     * @return the index page
     */
    @GetMapping("/")
    public String index(final Model model) {
        final Board board = gameService.getBoard();

        model.addAttribute("board", board);
        model.addAttribute("currentPlayer", gameService.getBoard().getCurrentPlayer().toString());
        model.addAttribute("winner", null);

        return "index";
    }

    /**
     * Handles the move request.
     *
     * @param pit the index of the pit to move the stones from.
     * @return the response, either an error message or the updated board
     * with the moves made.
     */
    @PostMapping("/move")
    @ResponseBody
    public GameControllerResponse makeMove(@RequestParam("pit") int pit) {
        final GameControllerResponse response = new GameControllerResponse();

        response.setCurrentPlayer(gameService.getBoard().getCurrentPlayer().toString());

        try {
            final List<Move> moves = gameService.makeMove(pit);
            response.setCurrentPlayer(gameService.getBoard().getCurrentPlayer().toString());
            response.setMoves(moves);
        } catch (GameLogicException e) {
            response.setError(e.getMessage());
        }

        return response;
    }

    /**
     * Handles the demo request.
     *
     * @return the response with the moves made and the winner.
     */
    @PostMapping("/demo")
    @ResponseBody
    public GameControllerResponse makeDemoMoves() {
        final GameControllerResponse response = new GameControllerResponse();

        response.setCurrentPlayer(gameService.getBoard().getCurrentPlayer().toString());

        final List<Move> moves = new ArrayList<>();

        while (!gameService.isGameOver()) {
            final int move = gameService.getRandomPitIndex();

            try {
                moves.addAll(gameService.makeMove(move));
                response.setCurrentPlayer(gameService.getBoard().getCurrentPlayer().toString());
            } catch (GameLogicException e) {
                // Ignore invalid moves, since this is a demo.
            }
        }

        response.setWinner(gameService.getWinnerString());
        response.setMoves(moves);

        return response;
    }

    /**
     * Handles the restart request.
     *
     * @return a redirect to the index page with the game reset.
     */
    @PostMapping("/restart")
    public String restartGame() {
        gameService.resetGame();
        return "redirect:/";
    }

}
