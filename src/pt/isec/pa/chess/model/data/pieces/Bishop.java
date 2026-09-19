package pt.isec.pa.chess.model.data.pieces;

import java.util.ArrayList;
import java.util.List;
/**
 * Class representation of a bishop piece.
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
public class Bishop extends Piece{

    /**
     * Constructor for the bishop piece, setting the starting position, color
     * @param x starting column position of this piece
     * @param y starting row position of this piece
     * @param c color of this piece
     * */
    public Bishop(char x,int y, Color c) {
        super(x,y, c);
        setType( c == Color.WHITE ? 'B' : 'b');
    }
    /**
     * returns moves on the 4 diagonals from bishop position
     * @return <code>List string</code> each string representing a possible move of the rook
     * */
    @Override
    public List<String> getPossibleMoves(ArrayList<Piece> pieceList){
            List<String> possibleMoves = new ArrayList<>();

        return possibleMovesDiagonal(pieceList,possibleMoves);

    }

}
