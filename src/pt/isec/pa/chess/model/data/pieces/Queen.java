package pt.isec.pa.chess.model.data.pieces;

import java.util.ArrayList;
import java.util.List;
/**
 * Class representation of a queen piece.
 *  <p>Since the {@link Piece} already implements the full logic of a queens movements
 *  there is little to no need of overriding methods</p>
 *
 * @see Piece
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 */
public class Queen extends Piece {
    /**
     * constructor method to correctly set the Queen's type
     * @param x starting column position of this piece
     * @param y starting row position of this piece
     * @param c color of this piece
     * */
    public Queen(char x, int y, Color c) {
        super(x, y, c);
        setType( c == Color.WHITE ? 'Q' : 'q');
    }

}

