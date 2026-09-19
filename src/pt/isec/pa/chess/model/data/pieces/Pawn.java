package pt.isec.pa.chess.model.data.pieces;

import java.util.ArrayList;
import java.util.List;
/**
 * Class representation of a Pawn piece.
 *
 *
 * @see Piece
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 */

public class Pawn extends Piece{
    /**
     * boolean representation if the pawn as moved or not
     * */
    boolean isFirstMove;
        /**
         * Constructor for a pawn piece, setting the variables introduced in this class
         * @param x starting column position of this piece
         * @param y starting row position of this piece
         * @param c color of this piece
         * */
    public Pawn(char x, int y, Color c) {
        super(x, y, c);
            setmaxX(x);
            setMaxY(1);
            isFirstMove = true;
            setType( c == Color.WHITE ? 'P' : 'p');
        setCanMoveBackwards(false);
    }
    /**
     * Override of original setPosition from {@link Piece} method to update if a move as been made
     * @param x char representing new column to set
     * @param i int representing new column to set
     * */
    @Override
    public void setPosition(char x, int i) {
        super.setPosition(x, i);
        isFirstMove= false;
    }
    /**
     * Creates a list to save every possible move, one space diagonally included
     * @param pieceList list of pieces provided by board
     * @return <code>list string</code> of every available move by the pawn
     *
     * */

    @Override
    public List<String> getPossibleMoves(ArrayList<Piece> pieceList) {
        List<String> moves = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? 1 : -1; // Brancos sobem (+1), pretos descem (-1)

        int newRow = getRow() + direction;
        if (newRow >= 1 && newRow <= 8 &&
                !checkPiecePosition(pieceList,getColumn(),newRow)) {
            moves.add(getColumn() + "" + newRow);
        }

        if (isFirstMove) {
            int twoStepsRow = getRow() + (2 * direction);
            if (twoStepsRow >= 1 && twoStepsRow <= 8
                    && !checkPiecePosition(pieceList,getColumn(),twoStepsRow)) {
                moves.add(getColumn() + "" + twoStepsRow);
            }
        }
        // Always include diagonal capture squares
        if (newRow >= 1 && newRow <= 8) {
            // Right diagonal
            if (getColumn() < 'h') {
                moves.add((char) (getColumn() + 1) + "" + newRow);
            }
            // Left diagonal
            if (getColumn() > 'a') {
                moves.add((char) (getColumn() - 1) + "" + newRow);
            }
        }


        return moves;
    }
}
