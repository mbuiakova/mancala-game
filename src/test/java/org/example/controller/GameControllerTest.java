package org.example.controller;

import org.example.exception.ErrorCode;
import org.example.exception.GameLogicException;
import org.example.model.Board;
import org.example.model.Move;
import org.example.model.Player;
import org.example.model.Winner;
import org.example.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void index_shouldReturnIndexPage() throws Exception {
        Board board = new Board();
        when(gameService.getBoard()).thenReturn(board);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("board", board))
                .andExpect(model().attribute("currentPlayer", board.getCurrentPlayer().toString()));
    }

    @Test
    void makeMove_shouldReturnUpdatedBoard() throws Exception {
        Board board = new Board();
        Move move = new Move(0, 1);
        when(gameService.getBoard()).thenReturn(board);
        when(gameService.makeMove(anyInt())).thenReturn(Collections.singletonList(move));

        mockMvc.perform(post("/move").param("pit", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPlayer").value(board.getCurrentPlayer().toString()))
                .andExpect(jsonPath("$.moves[0].fromPitIndex").value(move.getFromPitIndex()))
                .andExpect(jsonPath("$.moves[0].toPitIndex").value(move.getToPitIndex()));
    }

    @Test
    void makeMove_shouldReturnErrorOnInvalidMove() throws Exception {
        Board board = new Board();
        when(gameService.getBoard()).thenReturn(board);
        when(gameService.makeMove(anyInt())).thenThrow(new GameLogicException(ErrorCode.EMPTY_PIT, Player.ONE, 0));

        mockMvc.perform(post("/move").param("pit", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void makeDemoMoves_shouldReturnMovesAndWinner() throws Exception {
        final Board board = new Board();
        final Move move = new Move(0, 1);

        when(gameService.getBoard()).thenReturn(board);
        when(gameService.getRandomPitIndex()).thenReturn(0);
        when(gameService.isGameOver()).thenReturn(false).thenReturn(true);
        when(gameService.makeMove(anyInt())).thenReturn(Collections.singletonList(move));
        when(gameService.getWinnerString()).thenReturn("Player One wins!");

        mockMvc.perform(post("/demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPlayer").value(board.getCurrentPlayer().toString()))
                .andExpect(jsonPath("$.moves[0].fromPitIndex").value(move.getFromPitIndex()))
                .andExpect(jsonPath("$.moves[0].toPitIndex").value(move.getToPitIndex()))
                .andExpect(jsonPath("$.winner").value("Player One wins!"));
    }

    @Test
    void restartGame_shouldRedirectToIndex() throws Exception {
        mockMvc.perform(post("/restart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }
}