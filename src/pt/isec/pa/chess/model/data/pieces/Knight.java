package pt.isec.pa.chess.model.data.pieces;

import java.util.ArrayList;
import java.util.List;
/**
 * Class representation of a Knight piece.
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
public class Knight extends Piece {
    /**
     * Constructor for the knight piece, setting the starting position and color
     * @param x starting column position of this piece
     * @param y starting row position of this piece
     * @param c color of this piece
     * */
    public Knight(char x, int y, Color c) {
        super(x, y, c);
        setType( c == Color.WHITE ? 'N' : 'n');
    }
        /**
         * creates a new move list to save every available move of the rook
         * @return <code>List String</code> of every available move
         * */
    @Override
    public List<String> getPossibleMoves(ArrayList<Piece> pieceList) {
       List<String> moveslist = new ArrayList<>(); // Clear the move list before calculating new moves

        int[][] moves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : moves) {
            char newX = (char) (getColumn() + move[0]);
            int newY = getRow() + move[1];

            if (newX >= 'a' && newX <= 'h' && newY >= 1 && newY <= 8) {
                    moveslist.add("" + newX + newY);
            }
        }

        return moveslist;
    }
}