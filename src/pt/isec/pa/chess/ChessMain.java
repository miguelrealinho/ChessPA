package pt.isec.pa.chess;

import javafx.application.Application;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.pieces.Piece;
import pt.isec.pa.chess.ui.MainJFX;

/**
 * Main entry point for the Chess application.
 * Launches the JavaFX application MainJFX.
 */
public class ChessMain {

    /**
     * Launches the JavaFX application by starting the MainJFX class.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Application.launch(MainJFX.class, args);


    }
}
