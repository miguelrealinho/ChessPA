package pt.isec.pa.chess.model.data.pieces;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all chess pieces.
 * <p>
 * Encapsulates shared properties such as position and color,
 * and defines common behavior to be implemented by specific piece types
 *
 * <p>Each subclass must define its own movement logic by using methods provided by this class
 * or implement their own
 *
 *
 * @see Pawn
 * @see Rook
 * @see Knight
 * @see Bishop
 * @see Queen
 * @see King
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 */


public abstract class Piece implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
    /**
     * integer representation of piece row location
     * */
    private int row;
    /**
     * char representation of piece column location
     * */
    private char column;
    /**
     * the max squares a piece can move on the x axis
     * */
    private int maxX = 8;
    /**
     * the max squares a piece can move on the y axis
     * */
    private int maxY = 8;
    /**
     * boolean if a piece can move backwards
     * */
    private boolean canMoveBackwards = true;
    /**
     * color of the piece
     * */
    private Color color;
    /**
     * char representation of piece type
     * */
    private char type;

    /**
     * method that returns all of the pieces possible moves
     * @param PieceList boards list of all pieces
     * @return <code>list</code> created of possible moves
     * */
    public List<String> getPossibleMoves(ArrayList<Piece> PieceList){
        List<String> possibleMoves = new ArrayList<>();
        possibleMovesDiagonal(PieceList,possibleMoves);
        possibleMovesHV(PieceList,possibleMoves);
        return possibleMoves;
    }
/**
 * method to add a move to the movelist
 * @param moveList possible moveset for a piece
 *
 * */
    public void setMoveList(List<String> moveList) {
        this.moveList = moveList;
    }
 /**
  * protected moveList
  * */
    protected List<String> moveList;


    /**
     * returns type of this piece
     * @return various types of this piece
     * */
    public char getType() {
        return type;
    }

    /**
     * sets type of this piece
     * @param type new type to set
     * */
    public void setType(char type) {
        this.type = type;
    }

    /**
     * Represents the color of a chess piece or player.
     * Used to distinguish between the two sides in a chess game.
     */
    public enum Color{
        BLACK, WHITE
    }

    /**
     * gets color of this piece
     * @return returns this pieces color
     * */

    public Color getColor() {
        return color;
    }

    /**
     * sets color of this piece
     * @param color new color to set
     * */

    public void setColor(Color color) {
        this.color = color;
    }
    /**
     * gets row position of this piece
     * @return returns this pieces row
     * */

    public int getRow() {
        return row;
    }

    /**
     *
     * sets row position of this piece
     * @param row new row to set
     * */

    public void setRow(int row) {
        this.row = row;
    }
    /**
     *
     * gets column position of this piece
     * @return returns this pieces column
     * */

    public char getColumn() {
        return column;
    }
    /**
     *
     * sets column position of this piece
     * @param column new column to set
     * */
    public void setColumn(char column) {
        this.column = column;
    }
    /**
     *
     * sets maxX  of this piece
     * @param maxX new x axis limit
     * */
    public void setmaxX(int maxX) {
        this.maxX = maxX;
    }
    /**
     *
     * sets maxY  of this piece
     * @param y new y axis limit
     * */
    public void setMaxY(int y) {
        this.maxY = y;
    }
    /**
     *
     * sets if piece can move backwards from given boolean
     * @param canMoveBackwards given boolean to use to set
     * */
    public void setCanMoveBackwards(boolean canMoveBackwards) {
        this.canMoveBackwards = canMoveBackwards;
    }
    /**
     *
     * gets maxX  of this piece
     * @return <code>char</code> representing the current column of this piece
     * */
    public int getMaxX() {
        return maxX;
    }
    /**
     *
     * gets maxY  of this piece
     *@return <code>int</code> representing the current row of this piece
     * */
    public int getMaxY() {
        return maxY;
    }
    /**
     *
     * returns if piece can move backwards
     * @return <code>true</code> if piece can move backwards
     *         <code>false</code> otherwise
     * */
    public boolean getCanMoveBackwards() {
        return canMoveBackwards;
    }
    /**
     * Constructor for a Piece, only usable for classes that extend Piece
     * @param x column of piece
     * @param y row of piece
     * @param c color of piece
     * */

    protected Piece(char x,int y,Color c){
        this.row = y;
        this.column = x;
        this.color = c;

        type = (c == Color.WHITE) ?  'z' : 'z';

        moveList = new ArrayList<>();
    }
    /**
     * validates if the given position is this pieces position
     * @param PieceList piece list provided by board
     * @param column column position to compare with own
     * @param row row position to compare with own
     * @return <code>true</code> if position is the same
     *         <code>false</code> otherwise
     * */
    public boolean checkPiecePosition(ArrayList<Piece> PieceList, char column, int row) {
        for (Piece piece : PieceList) {
            if (piece.getRow() == row && piece.getColumn() == column) {
                return true;
            }
        }
        return false;
    }
        /**
         * Gives possible moves in the 4 diagonals from the piece position
         * only stopping at <b>any</b> piece that may appear on path, doesnt recognise game rules
         * @param pieceList list of all pieces on the board
         * @param moves movelist created for possiblemoves storage
         * @return <code>List strings</code> of available moves, empty if none
         * */
    public List<String> possibleMovesDiagonal(ArrayList<Piece> pieceList,List<String> moves){
        // Each pair represents a diagonal direction: (dx, dy)
        int[][] directions = {
                {1, 1},   // up-right
                {-1, 1},  // up-left
                {1, -1},  // down-right
                {-1, -1}  // down-left
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            for (int i = 1; i <= getMaxX(); i++) {
                char newX = (char) (getColumn() + dx * i);
                int newY = getRow() + dy * i;

                if (newX < 'a' || newX > 'h' || newY < 1 || newY > getMaxY()) break;
                if (!canMoveBackwards && dy < 0) break;

                if (!checkPiecePosition(pieceList, newX, newY)) { //if no piece
                    moves.add("" + newX + newY);
                } else {
                        //if there's a piece, add square and leave
                        moves.add("" + newX + newY);

                    break;
                }
            }
        }

        return moves;
    }
    /**
     * Gives possible moves in the horizontal and vertical planes from the piece position
     * only stopping at <b>any</b> piece that may appear on path, doesnt recognise game rules
     * @param PieceList list of all pieces on the board
     * @param moves movelist created for possiblemoves storage
     * @return <code>List strings</code> of available moves, empty if none
     * */
    public List<String> possibleMovesHV(ArrayList<Piece> PieceList, List<String>moves){
        for (int i = 1; i <= getMaxX(); i++) {
            char newColumn = (char) (getColumn() - i);
            if (newColumn >= 'a') {
                    moves.add(newColumn + "" + getRow());
                    if (checkPiecePosition(PieceList,newColumn,getRow()))break;
            }
        }

        for (int i = 1; i <= getMaxX(); i++) {
            char newColumn = (char) (getColumn() + i);
            if (newColumn <= 'h') {

                    moves.add(newColumn + "" + getRow());
                    if (checkPiecePosition(PieceList,newColumn,getRow()))break;
            }
        }
        for (int j = 1; j <= maxY; j++) {
            int newRow = row + j;
            if (newRow <= 8) {
                    moves.add(column + "" + newRow);
                    if (checkPiecePosition(PieceList,getColumn(),newRow))break;
            }
        }

        if (canMoveBackwards) {
            for (int j = 1; j <= maxY; j++) {
                int newRow = row - j;
                if (newRow >= 1) {
                        moves.add(column + "" + newRow);
                        if (checkPiecePosition(PieceList,getColumn(),newRow))break;
                }
            }
        }

        return moves;
    }

        /**
         * sets this piece position
         * @param x new column position to set
         * @param i new row position to set
         * */
    public void setPosition(char x, int i){
        column = x;
        row = i;
    }
        /**
         * Override of Object to string to give the string representation of the piece
         * @return  <code>String</code> of representation, for example Ke1
         * */
    @Override
    public String toString(){
        return "" + this.type + this.column + this.row;
    }

    /**
     * Determines whether this piece is equal to another object.
     * <p>
     * Two pieces are considered equal if they have the same position
     * (row and column) and the same color.
     *
     * @param obj the object to compare with
     * @return <code>true</code> if the specified object is equal to this piece;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Piece piece = (Piece) obj;
        return row == piece.row && column == piece.column && color == piece.color;
    }
    /**
     * Returns a hash code value for the piece based on its position and color.
     * <p>
     * This ensures that pieces that are equal according to {@link #equals(Object)}
     * will return the same hash code.
     *
     * @return the hash code for this piece
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column, color);
    }
}
