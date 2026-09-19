package pt.isec.pa.chess.model.data.board;

import pt.isec.pa.chess.model.ModelLog;
import pt.isec.pa.chess.model.data.factory.*;
import pt.isec.pa.chess.model.data.pieces.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static pt.isec.pa.chess.model.data.pieces.Piece.Color.BLACK;
import static pt.isec.pa.chess.model.data.pieces.Piece.Color.WHITE;


/**
 * Manages the Board providing methods to interact with pieces, including movement and removal operations.
 *<p>Uses {@link PieceFactory} to create new pieces using the factory pattern.</p>
 *
 * @see PieceFactory
 * @see Piece
 * @see Pawn
 * @see Queen
 * @see King
 * @see Rook
 * @see Bishop
 * @see Knight
 *
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 * */

public class Board implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * ArrayList to save all the active pieces, if a piece is not in the array, it as been removed
     * */
    ArrayList pieceList;
    /**
     * Constructor for a new board with an empty piece list
     * */
    public Board(){
        pieceList = new ArrayList<Piece>();
    }
    /**
     * Constructor for a new board with an existing piece list
     * @param list a list of all the pieces
     * */
    public Board(ArrayList<Piece> list){
        pieceList = new ArrayList<>();
        pieceList.addAll(list);

    }

    /**
     * Initializes the pieces in their correct starting positions
     * */
    public void initPieces(){
        addPiece(PieceFactory.createPiece("ra8"));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.KNIGHT, 'b', 8, BLACK));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.BISHOP, 'c', 8, BLACK));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.QUEEN, 'd', 8, BLACK));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.KING, 'e', 8, BLACK));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.BISHOP, 'f', 8, BLACK));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.KNIGHT, 'g', 8, BLACK));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.ROOK, 'h', 8, BLACK));


        for(int i = 0; i < 8; i++){
            addPiece(PieceFactory.createPiece(PieceFactory.PieceType.PAWN, (char)('a' + i), 7, BLACK));
        }

        for (int i = 0; i < 8 ; i++) {
            addPiece(PieceFactory.createPiece(PieceFactory.PieceType.PAWN, (char)('a' + i), 2, WHITE));
        }

        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.ROOK, 'a', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.KNIGHT, 'b', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.BISHOP, 'c', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.QUEEN, 'd', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.KING, 'e', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.BISHOP, 'f', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.KNIGHT, 'g', 1, WHITE));
        addPiece(PieceFactory.createPiece(PieceFactory.PieceType.ROOK, 'h', 1, WHITE));
    }

    /**
     * getter for the piece list
     * @return returns the list of pieces
     * */
    public ArrayList getPieceList() {
        return pieceList;
    }
        /**
         * method to add a already created piece to the piece list
         * @param piece a created piece of any type to be added
         * */
    public void addPiece(Piece piece){
        pieceList.add(piece);
    }

    /**
     * removes the piece on the given location from the piece list
     * @param x the column of the piece to remove
     * @param y the row of the piece to remove
     * */
    public void removePiece(char x, int y) {
        Piece toRemove = getPiece(x, y);

        if (toRemove != null) {
            pieceList.remove(toRemove);
        }
    }

    /**
     * Moves a specific rook to a specific location, used in case of castle to move the rook
     * @param pos the position in wich the king is moving, to detect what type of castle it is
     * @return <code>true</code> rook moved successfully;
     *         <code>false</code> otherwise.
     * */
    private boolean moveRook(String pos) {
        Piece rook = null;
        char targetCol = 'd';

        switch(pos) {
            case "c1":
                rook = getPiece('a', 1);
                targetCol = 'd';
                break;
            case "g1":
                rook = getPiece('h', 1);
                targetCol = 'f';
                break;
            case "c8":
                rook = getPiece('a', 8);
                targetCol = 'd';
                break;
            case "g8":
                rook = getPiece('h', 8);
                targetCol = 'f';
                break;
        }

        if (rook != null) {
            rook.setPosition(targetCol, rook.getRow());
            return true;
        }
        return false;
    }

    /**
     * moves a given piece to a target location, without verification,
     * also checks if there is a piece at target location and removes it.
     * If it's a castle move, also moves the apropriate rook.
     * @param dx Original column of piece to move
     * @param dy Original row of piece to move
     * @param x destiny column of piece to move
     * @param y destiny row of piece to move
     * @return <code>true</code> piece moved successfully;
     *         <code>false</code> otherwise.
     *
     *
     * */
    public boolean movePiece(char dx,int dy,char x,int y) {    //dx,dy posiçao da peça a mover
        Piece p = getPiece(dx, dy);
        if(isOccupied(""+x+y)){  removePiece(x,y);   }

        if (iscastling(dx,dy,x,y)){
            //move Rook
            moveRook(""+x+y);
        }


        p.setPosition(x, y);

        return true;
    }

    /**
     * returns the Piece at target location
     * @param x target column of piece
     * @param y target row of piece
     * @return <code>Piece</code> if there was a piece at target location;
     *         <code>null</code> otherwise.
     * */
    public Piece getPiece(char x,int y){
        for(Object piece : pieceList){
            Piece p = (Piece)piece;
            if(p.getRow() == y && p.getColumn() == x){
                return p;
            }
        }
        return null;
    }

    /**
     * checks target king of its first move
     * @param x target column of king
     * @param y target row of king
     * @return <code>true</code> if king still hasn't moved
     *          <code>false</code> if king has moved
     * */
    private boolean isKingFirstMove(char x, int y){
        for(Object piece : pieceList){
            Piece p = (Piece)piece;
            if(p.getRow() == y && p.getColumn() == x){
                if(p instanceof King){
                    King king = (King)p;
                    return king.isFirstMove();
                }
            }
        }
        return false;
    }
    /**
     * returns if the piece at location is a pawn
     * @param x target column of piece to view
     * @param y target row of piece to view
     * @return <code>true</code> piece is a pawn
     *          <code>false</code> piece is not a pawn
     *          <code>null</code> there is not a piece at location
     * */

    public boolean isPawn(char x,int y){
        return getPiece(x,y) instanceof Pawn;
    }


    /**
     * verifies if given move is a castle attempt
     *
     * @param dx Original column of king
     * @param dy original row of king
     * @param x target column of king
     * @param y target row of king
     * @return <code>true</code> move is a castle attempt
     *          <code>false</code> move is not a castle attempt
     * */

    public boolean iscastling(char dx, int dy,char x ,int y){
        String Destination = "" + x + y;
            //quem esta a dar roque e para que lado
        switch(Destination){
            case("c1"):
                //roque grande dos brancos
                    for(Object obj : pieceList){
                        Piece piece = (Piece)obj;

                        if(piece.getRow() == y && piece.getColumn() == 'a'){
                            if(piece instanceof Rook){
                                Rook rook = (Rook)piece;
                                if(rook.isFirstMove() && isKingFirstMove(dx,dy)){
                                    if(!isPositionAttacked('c',1, WHITE,null)&&
                                            !isPositionAttacked('d',1, WHITE,null) &&
                                            !isPositionAttacked('e',1, WHITE,null)){

                                                return true;
                                    }
                                }
                            }
                        }
                    }
                return false;
            case("g1"):

                //roque pequeno dos brancos
                for(Object obj : pieceList){
                    Piece piece = (Piece)obj;

                    if(piece.getRow() == y && piece.getColumn() == 'h'){
                        if(piece instanceof Rook){
                            Rook rook = (Rook)piece;
                            if(rook.isFirstMove() && isKingFirstMove(dx,dy)){
                                if(!isPositionAttacked('g',1, WHITE,null)&&
                                        !isPositionAttacked('f',1, WHITE,null) &&
                                        !isPositionAttacked('e',1, WHITE,null)){
                                    return true;
                                }
                            }
                        }
                    }
                }

                return false;
            case("c8"):

                //roque grande dos pretos
                for(Object obj : pieceList){
                    Piece piece = (Piece)obj;

                    if(piece.getRow() == 8 && piece.getColumn() == 'a'){
                        if(piece instanceof Rook){
                            Rook rook = (Rook)piece;
                            if(rook.isFirstMove() && isKingFirstMove(dx,dy)){
                                if(!isPositionAttacked('c',8, Piece.Color.BLACK,null)&&
                                        !isPositionAttacked('d',8, BLACK,null) &&
                                        !isPositionAttacked('e',8, BLACK,null)){
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            case("g8"):
                //roque pequeno dos pretos
                for(Object obj : pieceList){
                    Piece piece = (Piece)obj;

                    if(piece.getRow() == 8 && piece.getColumn() == 'h'){
                        if(piece instanceof Rook){
                            Rook rook = (Rook)piece;
                            if(rook.isFirstMove() && isKingFirstMove(dx,dy)){
                                if(!isPositionAttacked('g',8, Piece.Color.BLACK,null)&&
                                        !isPositionAttacked('f',8, BLACK,null) &&
                                        !isPositionAttacked('e',8, BLACK,null)){
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;

        }


        return false;
    }
   /**
    * returns piece string at target location
    * @param x target column of piece
    * @param y target row of piece
    * @return <code>String</code> piece's representation as a string
    *          <code>null</code> there is not a piece at location
    *
    * */
    public String seePieceAt(char x,int y) {
        return getPiece(x, y).toString();
    }
    /**
     * returns color of piece at target location
     * @param x target column of piece
     * @param y target row of piece
     * @return <code>Color</code> piece's color
     *          <code>null</code> there is not a piece at location
     *
     * */

    public Piece.Color getColor(char x,int y) {
        for (Object piece : pieceList) {
            Piece p = (Piece)piece;
            if(p.getRow() == y && p.getColumn() == x){
                return p.getColor();
            }
        }
        return null;
    }

    /**
     * validates if a given move is in the target piece move list
     * @param dx original column of piece
     * @param dy original row of piece
     * @param x target column of piece
     * @param y target row of piece
     * @return <code>true</code> move is valid in target piece
     *          <code>false</code> otherwise
     *
     * */

    public boolean isValidMove(char dx, int dy,char x,int y) {
        String moveToMake = "" + x + y;
            Piece piece = getPiece(dx,dy);
            for (Object move : piece.getPossibleMoves(pieceList)){
                String pmove = (String)move;
                if(pmove.equals(moveToMake)){
                    return true;
                }
            }

        return false;
    }

    /**
     * validates if a given position is occupied by a piece
     * @param position string representation of a position, column+row
     * @return <code>true</code> position is occupied
     *          <code>false</code> otherwise
     *
     * */

    public boolean isOccupied(String position) {
        for(Object it : pieceList){
            Piece piece = (Piece)it;
            if(position.equals("" + piece.getColumn() + piece.getRow())){
                return true;
            }
        }
        return false;
    }

    /**
     * retrieves string representation of position of a king of a given color
     * <p>always assumes there is an existing king</p>
     * @param color color of king to track
     * @return <code>string</code> position is occupied
     *
     * */
    public String getKingByColor(Piece.Color color){
        if(color == WHITE){
            for(Object obj : pieceList){
                Piece piece = (Piece) obj;
                if(piece.getType() == 'K'){
                    return ""+piece.getColumn()+piece.getRow();
                }
            }
        }else{
            for(Object obj : pieceList){
                Piece piece = (Piece) obj;
                if(piece.getType() == 'k'){
                    return ""+piece.getColumn()+piece.getRow();
                }
            }
        }
        return "";
    }
    /**
     * returns the move list of a given piece
     * @param x original column of piece
     * @param y original row of piece
     * @return <code>List</code> of the available moves by a piece
     *
     * */

    public List<String> getPieceMoveList(char x, int y){
        Piece piece = getPiece(x, y);
        if(piece != null){return getPiece(x,y).getPossibleMoves(getPieceList());}

        List<String> moves = new ArrayList<>();
        return moves;

    }

    /**
     * returns the text representation of the piece list
     * @return <code>List</code> of the pieces text representation
     *
     * */

    public List<String> getPieceMoveList(char x, int y){
        return getPiece(x,y).getPossibleMoves(getPieceList());
    }

    public String getBoardString(){
        StringBuilder textRep = new StringBuilder();
        for(Object it : getPieceList()){
            Piece piece = (Piece)it;
            textRep.append(piece.toString()).append(",");
        }

        if (textRep.length() > 0) {
            textRep.setLength(textRep.length() - 1); // Remove the last comma if there are pieces
        }


        return textRep.toString();
    }
    /**
     * removes every piece from the board
     * */
    public void clearBoard(){
        pieceList.clear();
    }

    /**
     * creates a new board with a copy of all the pieces, if there is a piece to ignore,
     * removes it from the new board, and checks if given position is being attacked
     * @param x column of position to check
     * @param y row  of position to check
     * @param positionToIgnore string representation of a position to ignore, column+row
     * @return <code>piece</code> that is attacking given position
     *         <code>null</code>   if no piece is attacking position
     *
     * */

    public Piece pieceAttPos(char x,int y,String positionToIgnore){
            // color refers to attacked piece
            Board newboard = new Board(pieceList);
            if (positionToIgnore != null && !positionToIgnore.isEmpty()) {
                newboard.removePiece(positionToIgnore.charAt(0),Character.getNumericValue(positionToIgnore.charAt(1)));
            }
            for(Object it : newboard.getPieceList()){
                Piece piece = (Piece)it;

                    if(piece.getPossibleMoves(newboard.getPieceList()).contains(""+x+y)){
                        return piece;

                    }

            }
            return null;
    }


    /**
     creates a new board with a copy of all the pieces, if there is a piece to ignore,
     * removes it from the new board, and checks if given position is being attacked by a specific color
     * @param x column of position to check
     * @param y row  of position to check
     * @param color color of attacked piece
     * @param positionToIgnore string representation of a position to ignore, column+row
     * @return <code>true</code> given position is being attacked
     *         <code>false</code>   otherwise
     *
     * */

    public boolean isPositionAttacked(char x,int y,Piece.Color color, String positionToIgnore){
        // color refers to attacked piece
       Board newboard = new Board(pieceList);
        if (positionToIgnore != null && !positionToIgnore.isEmpty()) {
            newboard.removePiece(positionToIgnore.charAt(0),Character.getNumericValue(positionToIgnore.charAt(1)));
        }
        for(Object it : newboard.getPieceList()){
            Piece piece = (Piece)it;
            if(piece.getColor() != color){
                if(piece.getPossibleMoves(newboard.getPieceList()).contains(""+x+y)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether there is exactly one king present on the board for the given color.
     *
     * <p>This method verifies if a king piece exists for the specified player color ("WHITE" or "BLACK").
     * It relies on the {@code getKingByColor} method to determine the presence of the king.
     *
     * @param color The color of the player to check ("WHITE" or "BLACK").
     * @return {@code true} if a king is found for the specified color; {@code false} otherwise.
     */
        public boolean hasOneKing(String color){
        if(color.equals("WHITE")){
            String king = getKingByColor(WHITE);
            if(king != null){
                return true;
            }
        } else if(color.equals("BLACK")){
            String king = getKingByColor(BLACK);
            if(king != null){
                return true;
            }
        }
        return false;
    }
    /**
     * Returns all pieces in the board as a String
     * */
    public String getBoardStringForFile() {
        StringBuilder sb = new StringBuilder();
        for (Object piece : pieceList) {
            Piece p = (Piece) piece;
            sb.append(p.toString()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Remove last comma
        }
        return sb.toString();
    }
}
