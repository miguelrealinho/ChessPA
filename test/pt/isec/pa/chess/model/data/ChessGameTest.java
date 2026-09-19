package pt.isec.pa.chess.model.data;

import org.junit.Test;
import pt.isec.pa.chess.model.ChessGameManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChessGameTest {


    private ChessGameManager chessGamemanager;
    private ChessGame chessGame;

    private void setUptest1(){
        chessGame = new ChessGame();
        chessGame.start("player1", "player2");

    }


    @Test
    public void test1(){
                setUptest1();
                assertTrue(!chessGame.isMoveValid('a',1,'h',8));
    }

}