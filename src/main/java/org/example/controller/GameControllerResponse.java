package org.example.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Move;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GameControllerResponse implements Serializable {
    private String currentPlayer;
    private String winner;
    private List<Move> moves;
    private String error;
}
