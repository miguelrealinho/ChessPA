package pt.isec.pa.chess.model.data.pieces;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representation of a King piece.
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

public class King extends Piece{
    /**
     * to know if the piece as moved
     * @return <code>true</code> if no movement was made
     *         <code>false</code> otherwise
     * */
    public boolean isFirstMove() {
        return isFirstMove;
    }
    /**
     * boolean setting if this rook as moved or not
     * */
    private boolean isFirstMove;
    /**
     * Constructor for the rook piece, setting the starting position, color and first move true
     * @param x starting column position of this piece
     * @param y starting row position of this piece
     * @param c color of this piece
     * */
    public King(char x, int y, Color c) {
        super(x, y, c);
        isFirstMove = true;
        setType( c == Color.WHITE ? 'K' : 'k');
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
     * returns every available move a king can make
     * <p>Also assumes the king can move two squares horizontally</p>
     * @return <code>List string</code> each string representing a possible move of the rook
     * */

    @Override
    public List<String> getPossibleMoves(ArrayList<Piece> pieceList) {
        List<String> moves = new ArrayList<>();
        char currentCol = getColumn();
        int currentRow = getRow();

        // 8 directions around the king
        for (int colDelta = -1; colDelta <= 1; colDelta++) {
            for (int rowDelta = -1; rowDelta <= 1; rowDelta++) {
                // Skip current position
                if (colDelta == 0 && rowDelta == 0) continue;

                char newCol = (char) (currentCol + colDelta);
                int newRow = currentRow + rowDelta;

                // Check board bounds
                if (newCol >= 'a' && newCol <= 'h' && newRow >= 1 && newRow <= 8) {
                    moves.add(newCol + "" + newRow);
                }
            }
        }
        for (int i=-2; i<=2;i++) {
            char newCol = (char) (currentCol + i);
            if (newCol >= 'a' && newCol <= 'h') {
                moves.add(newCol + "" + currentRow);
            }
        }

        return moves;
    }


    /**
     * returns this king's string representation
     * @return <code>String</code> king's representation as a string with representation of isfirstmove
     * */
    @Override
    public String toString(){
        return "" + getType() + getColumn() + getRow() + (isFirstMove ? '*' : "");
    }

}
