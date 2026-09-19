package pt.isec.pa.chess.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChessGameManagerTest {

    private ChessGameManager chessGamemanager;
    private void setUptest2(){
        this.chessGamemanager = new ChessGameManager();
        chessGamemanager.start("player1", "player2");
        chessGamemanager.makeMove('f',2,'f',3);
        chessGamemanager.makeMove('e',7,'e',6);
        chessGamemanager.makeMove('g',2,'g',4);
        chessGamemanager.makeMove('d',8,'h',4);
    }

    @Test
    public void test2(){
        setUptest2();
        assertTrue(this.chessGamemanager.isCheckMate());

    }
}