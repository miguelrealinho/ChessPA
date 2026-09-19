package pt.isec.pa.chess.model.memento;

public class Memento {
    private final String boardState;
    private final boolean whiteToMove;
    private final String currentPlayer;

    public Memento(String boardState, boolean whiteToMove, String currentPlayer) {
        this.boardState = boardState;
        this.whiteToMove = whiteToMove;
        this.currentPlayer = currentPlayer;
    }

    public String getBoardState() {
        return boardState;
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
