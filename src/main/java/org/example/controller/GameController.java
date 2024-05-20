package org.example.controller;

import org.example.exception.GameLogicException;
import org.example.model.Move;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.example.model.Board;
import org.example.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public GameController(GameService gameService) {
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
        model.addAttribute("currentPlayer", gameService.getBoard().getCurrentPlayer());
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
    public Map<String, Object> makeMove(@RequestParam("pit") int pit) {
        final Map<String, Object> response = new HashMap<>();
        response.put("currentPlayer", gameService.getBoard().getCurrentPlayer());

        try {
            final List<Move> moves = gameService.makeMove(pit);
            response.put("currentPlayer", gameService.getBoard().getCurrentPlayer());
            response.put("moves", moves);
        } catch (GameLogicException e) {
            response.put("error", e.getMessage());
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
    public Map<String, Object> makeDemoMoves() {
        final Map<String, Object> response = new HashMap<>();
        response.put("currentPlayer", gameService.getBoard().getCurrentPlayer());
        final List<Move> moves = new ArrayList<>();

        while (!gameService.getBoard().isGameOver()) {
            final int move = (int) (Math.random() * gameService.getBoard().getPits().length);

            try {
                moves.addAll(gameService.makeMove(move));
                response.put("currentPlayer", gameService.getBoard().getCurrentPlayer());
            } catch (GameLogicException e) {
                // Ignore invalid moves, since this is a demo.
            }
        }

        response.put("winner", gameService.determineWinner());
        response.put("moves", moves);

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
