package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.ModelLog;

import pt.isec.pa.chess.model.data.board.Board;
import pt.isec.pa.chess.model.data.factory.PieceFactory;
import pt.isec.pa.chess.model.data.pieces.Piece;
import pt.isec.pa.chess.model.data.saves.SaveLoadManager;
import pt.isec.pa.chess.model.memento.IMemento;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the state and logic of a chess game, including board setup, move validation,
 * turn tracking, and game conclusion detection (e.g., checkmate, stalemate).
 *
 * * <p>This class encapsulates the rules of chess and enforces valid gameplay. It also
 *  * supports saving and restoring game state using the Memento design pattern via
 *  * {@link ChessGameMemento}.
 *
 * @see Board
 * @see Piece
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 * */


public class ChessGame implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    /**
     * Variable to Store an instance of Board class
     * */
    private Board board;
    /**
     * Variable to change active player
     * */
    private boolean whiteToMove;
    /** Variable for check knowledge*/
    private boolean ischeck;
    /** Variable for game over knowledge*/
    private boolean isgameover;
    /** Variable for Promotion knowledge*/
    private boolean isPromotion;
    /** Variable for Editing knowledge*/
    private boolean isEditing;
    /** Variable for winner knowledge*/
    private Piece.Color winner;
    /** Player 1 name*/
    private String player1;
    /** Player 2 name*/
    private String player2;
    /** Variable for storage of last pawn that moved 2 squares*/
    private String lastTwoSquarePawnMove = null;
    /** Variable for storage of player promotion choice*/
    private char pieceChoice;
    /** Variable for storage of player color choice*/
    private boolean colorChoice;

    /**
     * Constructor of a ChessGame instance inicializing variables
     * */
    public ChessGame() {
        board = new Board();
        whiteToMove = true;
        ischeck = false;
        isgameover = false;
        winner = null;
    }
    /**
     * Method to start a new game
     * @param p1 Player1 name has a String
     * @param p2 Player2 name has a String
     *           Also inicializes the pieces
     * */
    public void start(String p1, String p2){
        // start new game
        this.board = new Board();
        player1 = p1;
        player2 = p2;
        whiteToMove = true;

        //garantir que um jogo novo começa
        isgameover = false;
        ischeck = false;
        isEditing = false;
        ModelLog.getInstance().clearLogs();

        board.initPieces();
    }

        /**
         * Saves a game to a .dat file
         * */

    public void saveGame(){
        SaveLoadManager.saveGame(this);
    }


    /**
     * Loads a game from a .dat file
     * @param file name of file to load
     * @return <code>true</code> loaded and was painted successfully;
     *         <code>false</code> otherwise.
     * */
    public boolean loadGame(String file){
        // start loaded game
        ChessGame loaded = SaveLoadManager.loadGame(file);
        if(loaded != null){
            board = loaded.getBoard();
            ModelLog.getInstance().addLog("Game loaded");
        } else{
            ModelLog.getInstance().addLog("não é possivel dar load.");
            return false;
        }

        isgameover = loaded.isgameover;
        ischeck = loaded.ischeck;
        whiteToMove = loaded.whiteToMove;
        player1 = loaded.player1;
        player2 = loaded.player2;


        return true;
    }

    /**
     * returns board instance
     * */
    private Board getBoard(){
        return board;
    }

    /**
     * getter for a piece color
     *          @param x Column position of the piece in the board 'a' to board size
     *          @param y Row position of the piece in the board 1 to board size
     *          @return <code>"WHITE"</code> if piece is white
     *                  <code>"BLACK"</code> if piece is black
     *                  <code>null</code> if there is no piece at position
     * */

    public String getPieceColor(char x ,int y){
        if(board.getColor(x,y) == Piece.Color.WHITE){
            return "WHITE";
        }else if(board.getColor(x,y) == Piece.Color.BLACK){
            return "BLACK";
        }
        return null;
    }

    /**
     * checks if movement is for en passent
     * @param dx original column
     * @param dy original row
     * @param x destiny column
     * @param y destiny row
     * @return <code>true</code> move is en passent;
     *         <code>false</code> otherwise.
     * */

    private boolean ispassent(char dx,int dy,char x,int y){
        if (board.isPawn(dx, dy)) {
            // Check if it's a diagonal move
            if (x != dx) {
                // If it's a diagonal move, there must be a piece to capture or it must be en passant
                return ("" + x + y).equals(lastTwoSquarePawnMove);

            }
            return true;
        }
        return false;
    }

    /**
     * Validates if move is valid
     * @param dx original column
     * @param dy original row
     * @param x destiny column
     * @param y destiny row
     * @return <code>true</code> if move is valid;
     *         <code>false</code> otherwise.
     * */
    public boolean isMoveValid(char dx, int dy, char x, int y){
        Piece.Color color;
        if ("WHITE".equals(getPieceColor(dx,dy))) {color = Piece.Color.WHITE;}
        else if("BLACK".equals(getPieceColor(dx,dy))){color = Piece.Color.BLACK;}
        else{return false;}

        if (!board.isValidMove(dx,dy,x,y)){return false;}

        if (color == board.getColor(x,y)){
            //se a peça no destino for da mesma cor o movimento nao é valido
            return false;
        }
        if (!ischeck) {
            // If the moving piece is a king, check if the destination is safe
            if (board.getKingByColor(color).equals("" + dx + dy)) {
                //check if is Castle movement
                if (board.iscastling(dx, dy, x, y)) {
                    return true;
                } else if (!board.iscastling(dx, dy, x, y) && Math.abs(x - dx) <= 1) {
                    // Check if the destination square is not attacked
                    if (board.isPositionAttacked(x, y, color, "")) return false;
                   return board.isValidMove(dx, dy, x, y);
                } else {
                    return false;
                }
            }

            //check if moving the piece generates a self check
            String kingposition = board.getKingByColor(color);
            char kingColumnChar = kingposition.charAt(0);
            int kingRow = Character.getNumericValue(kingposition.charAt(1));
            if (board.isPositionAttacked(kingColumnChar, kingRow, color, "" + dx + dy)) {
                return false;
            }

            // Handle pawn moves
            if (board.isPawn(dx,dy)) {

                if (board.isOccupied("" + x + y)) {
                    return true;
                }

                return ispassent(dx, dy, x, y);
            }
        }
        // For non-king pieces,if in check, check if they can block or capture the checking piece
        if (ischeck) return checkPossibleMoves(color, getCheckPiece(color),dx,dy, x, y);
        return board.isValidMove(dx,dy,x,y);
    }


    /**
     * Validates game over message
     *
     * @return <code>"checkmate" player</code> string with player who won
     *          <code>"Stalemate"</code> string with stalemate
     *          <code>null</code>if neither of above
     *
     * */

    public String isGameOver(){
        if(isgameover){
            if(verifyCheckMate(Piece.Color.WHITE)) {
                winner = Piece.Color.BLACK;
                return "CHECKMATE - " + player2 + " wins!";
            }
            if(verifyCheckMate(Piece.Color.BLACK)) {
                winner = Piece.Color.WHITE;
                return "CHECKMATE - " + player1 + " wins!";
            }
            if (!isKingInCheck(Piece.Color.WHITE) &&
                    !hasAnyValidMoves(Piece.Color.WHITE)) {
                return "STALEMATE";
            }
            if (!isKingInCheck(Piece.Color.BLACK) &&
                    !hasAnyValidMoves(Piece.Color.BLACK)) {
                return "STALEMATE";}
        }
        return null;
    }
    /**
     * returns isgameover value
     * @return <code>true</code> if true;
     *         <code>false</code> otherwise
     * */
    public boolean isGameOverBool(){
        return isgameover;
    }

    /**
     * Makes a given move if validated successfully
     *
     * @param dx original column
     * @param dy original row
     * @param x destiny column
     * @param y destiny row
     *
     * @return <code>true</code> if move is successfull
     *         <code>false</code> otherwise
     * */

    public boolean makeMove(char dx,int dy,char x,int y){
        if(!isgameover) {
            if (whiteToMove) {
                if (board.getColor(dx, dy) == Piece.Color.WHITE) {
                    if (isMoveValid(dx, dy, x, y)) {

                        if (ispassent(dx,dy,x,y) && x != dx) {
                            board.removePiece(x, y-1);
                        }

                        boolean moveResult = board.movePiece(dx, dy, x, y);
                        if (moveResult) {
                            if(y == 8 && board.isPawn(x, y)){
                                isPromotion = true;
                                return true; // dont toggle player or checkmate yet
                            }
                            // Check if this was a two-square pawn move
                            if (board.isPawn(x, y) && Math.abs(y - dy) == 2) {
                                lastTwoSquarePawnMove = "" + x + (y-1);
                            } else {
                                lastTwoSquarePawnMove = null;
                            }
                            togglePlayer();
                            // Update check status after move
                            boolean isGameOver = verifyCheckMate(Piece.Color.BLACK);
                            if (isGameOver) {
                                isgameover = true;
                            }
                            return moveResult;
                        }
                    }
                }
            } else if (board.getColor(dx, dy) == Piece.Color.BLACK) {
                if (isMoveValid(dx, dy, x, y)) {

                    if (ispassent(dx,dy,x,y) && x != dx) {
                        board.removePiece(x, y+1);
                    }

                    boolean moveResult = board.movePiece(dx, dy, x, y);
                    if (moveResult) {
                        if(y == 1 && board.isPawn(x, y)){
                            isPromotion = true;
                            return true; // dont toggle player or checkmate yet
                        }
                        // Check if this was a two-square pawn move
                        if (board.isPawn(x, y) && Math.abs(y - dy) == 2) {
                            lastTwoSquarePawnMove = "" + x + (y+1);
                        } else {
                            lastTwoSquarePawnMove = null;
                        }
                        togglePlayer();
                        // Update check status after move
                        boolean isGameOver = verifyCheckMate(Piece.Color.WHITE);
                        if (isGameOver) {
                            isgameover = true;
                        }
                        return moveResult;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Toggle for active player
     * */

    private void togglePlayer(){
        whiteToMove = !whiteToMove;
    }


    /**
     * getter for current player
     * @return <code>player1</code> if active player;
     *         <code>player2</code> if active player;
     * */

    public String getCurrentPlayer(){
        if(whiteToMove){
            return player1;
        }else{
            return player2;
        }
    }

    /**
     * getter for other player
     * @return <code>player1</code> if non-active player;
     *         <code>player2</code> if non-active player;
     * */
    public String getOtherPlayer(){
        if(whiteToMove){
            return player2;
        }else{
            return player1;
        }
    }


    /**
     * getter for full ListArray in board of the pieces
     * @return Board pieceList as ListArray of strings
     * */

    public String getCurrentBoard(){
        return board.getBoardString();
    }


    /**
     * removes every piece from the board
     * */
    public void clearBoard(){
        board = new Board();
        board.clearBoard();
        whiteToMove = true;
        ischeck = false;
        isgameover = false;
        isPromotion = false;
        winner = null;
        lastTwoSquarePawnMove = null;
        pieceChoice = '\0';
    }

    /**
     * this asks board if a position is being occupied
     * @param position valid position on the board first needs to be a lowercase letter second a number as in "a1"
     * @return <code>true</code> if true;
     *         <code>false</code> otherwise
     * */


    public boolean isPositionOccupied(String position){
        return board.isOccupied(position);
    }


    /**
     * returns isWhiteToMove
     * @return <code>true</code> if true;
     *         <code>false</code> otherwise
     * */


    public boolean isWhiteToMove(){
        return whiteToMove;
    }


    /**
     * validates king position to define ischeck variable
     * @param kingColor color of the king to check
     * @return <code>true</code> if true;
     *         <code>false</code> otherwise
     * */

    private boolean isKingInCheck(Piece.Color kingColor){
        String kingPosition = board.getKingByColor(kingColor);
        char kingColumn = kingPosition.charAt(0);
        int kingRow = Character.getNumericValue(kingPosition.charAt(1));

        if (board.isPositionAttacked(kingColumn, kingRow, kingColor,"")){
            ischeck = true;
            ModelLog.getInstance().addLog( kingColor + " king in check");
            return true;
        }

        ischeck = false;
        return false;
    }

    /**
     * Goes through every piece on the board to find the one attacking the king
     * @param kingColor color of the king being potentially attacked
     * @return <code>String</code> checking piece position as string;
     *         <code>null</code> if no piece is attacking the king
     * */

    private String getCheckPiece(Piece.Color kingColor) {

        String kingPosition = board.getKingByColor(kingColor);
        char kingColumn = kingPosition.charAt(0);
        int kingRow = Character.getNumericValue(kingPosition.charAt(1));

        Piece.Color opponentColor = (kingColor == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;

        // itera sobre as peças
        for (Object obj : board.getPieceList()) {
            Piece piece = (Piece) obj;
            if (piece.getColor() == opponentColor) {

                List<String> possibleMoves = piece.getPossibleMoves(board.getPieceList());
                if (possibleMoves.contains("" + kingColumn + kingRow)) {
                    // retorna a peça que esta a dar check ao rei
                    return "" + piece.getColumn() + piece.getRow();
                }
            }
        }


        return null;
    }


    /**
     * Validates if given king as any valid moves available, rendering him stuck
     * @param kingColor color of the king potentially stuck
     * @param possibleMoves List<String> if the valid moves of the king want to be saved
     * @return <code>true</code> if king isn't stuck;
     *         <code>false</code> otherwise;
     * */

    private boolean isKingStuck(Piece.Color kingColor, List<String> possibleMoves) {

        String kingPosition = board.getKingByColor(kingColor);
        char kingColumn = kingPosition.charAt(0);
        int kingRow = Character.getNumericValue(kingPosition.charAt(1));


        Piece king = board.getPiece(kingColumn, kingRow);

        List<String> kingPossibleMoves = king.getPossibleMoves(board.getPieceList());
        List<String> validKingMoves = new ArrayList<>();

        for (String move : kingPossibleMoves) { // remover posiçoes de peças amigas
            if (getPieceColor(move.charAt(0),Character.getNumericValue(move.charAt(1)))
                    == getPieceColor(kingColumn,kingRow)){
                continue;
            }
            validKingMoves.add(move);
        }


        List<String> kingverifiedMoves = new ArrayList<>();


        for(String kingMove : validKingMoves) {
            if (!board.isPositionAttacked(kingMove.charAt(0),Character.getNumericValue(kingMove.charAt(1)),kingColor,"")){
                // se movimento do rei nao esta a ser atacado
                kingverifiedMoves.add(kingMove);
            }
        }

        if (kingverifiedMoves.isEmpty()) { //se nao ha movimentos validos

            return true;
        } else {
            if (possibleMoves != null) {

                possibleMoves.addAll(kingverifiedMoves);
            }
            return false;
        }
    }

    /**
     * Validates every position of the kings side to check for possible check blocks of checking piece captures
     * @param kingColor color of attacked king
     * @param checkingPiecePosition position of checking piece as a String for example "a1"
     * @return <code>true</code> if true;
     *         <code>false</code> otherwise
     * */

    private boolean isKingSaveable(Piece.Color kingColor, String checkingPiecePosition) {

        if (checkingPiecePosition == null) {
            return true;
        }
        Piece.Color enemycolor;

        if (kingColor == Piece.Color.WHITE) {
            enemycolor = Piece.Color.BLACK;
        } else { enemycolor = Piece.Color.WHITE;}

        // Find king's position
        String kingPosition = board.getKingByColor(kingColor);
        char kingColumn = kingPosition.charAt(0);
        int kingRow = Character.getNumericValue(kingPosition.charAt(1));




        if (kingPosition == null) {

            return false;
        }

        // Get blocking squares between the king and checking piece (if blockable)
        List<String> blockingSquares = getBlockingSquares(kingPosition, checkingPiecePosition);


        for (String blockingSquare : blockingSquares) {
            char squareColumn = blockingSquare.charAt(0);
            int squareRow = Character.getNumericValue(blockingSquare.charAt(1));
            Piece piece = board.pieceAttPos(squareColumn,squareRow,checkingPiecePosition);
            if (piece != null) {return false;}
            if (piece.getColor() == kingColor
                    && (piece.getRow() != kingRow || piece.getColumn() != kingColumn)) {
                return isMoveValid(piece.getColumn(), piece.getRow(), squareColumn,squareRow);
            }
        }

        char squareColumn = checkingPiecePosition.charAt(0);
        int squareRow = Character.getNumericValue(checkingPiecePosition.charAt(1));

        //se uma peça pode comer o check
        if (board.isPositionAttacked(squareColumn, squareRow, enemycolor,kingPosition)){
            //se uma peça que nao o rei pode comer o check

            return true;
        }

        if (!board.isPositionAttacked(squareColumn, squareRow,kingColor,"")){
            //verifica se algum inimigo pode atacar a peça usado caso o rei possa comer, verifica se fica em check ou nao

            return false;
        }




        return false;
    }

    /**
     * determines the positions in between a king and the piece attacking it
     * @param kingPos position of the king as a String
     * @param checkerPos position of the piece attacking the king
     * @return ListString of available positions, is empty if there are none
     *
     *
     * */

    private List<String> getBlockingSquares(String kingPos, String checkerPos) {
        List<String> blockingSquares = new ArrayList<>();

        int kingCol = kingPos.charAt(0) - 'a';
        int kingRow = Character.getNumericValue(kingPos.charAt(1));
        int checkerCol = checkerPos.charAt(0) - 'a';
        int checkerRow = Character.getNumericValue(checkerPos.charAt(1));

        int dCol = checkerCol - kingCol;
        int dRow = checkerRow - kingRow;

        // Check if they're in line (horizontal, vertical, or diagonal)
        if (dCol != 0 && dRow != 0 && Math.abs(dCol) != Math.abs(dRow)) {
            // Not aligned — no blocking possible
            return blockingSquares;
        }

        int stepCol = Integer.signum(dCol);
        int stepRow = Integer.signum(dRow);

        int currentCol = kingCol + stepCol;
        int currentRow = kingRow + stepRow;

        while (currentCol != checkerCol || currentRow != checkerRow) {
            String square = "" + (char) ('a' + currentCol) + currentRow;
            blockingSquares.add(square);

            currentCol += stepCol;
            currentRow += stepRow;
        }

        return blockingSquares;
    }
    /**
     * verifies if a move is possible if there is a check on board
     * @param kingColor color of king in check
     * @param checkingPiecePosition position of piece attacking king
     * @param dx original Column of piece to move
     * @param dy original row of piece to move
     * @param x destiny column of piece to move
     * @param y destiny row of piece to move
     * @return <code>true</code> if true;
     *         <code>false</code> otherwise
     * */
    private boolean checkPossibleMoves(Piece.Color kingColor, String checkingPiecePosition,char dx,int dy,char x,int y) {
        //funçao para ver se move é valido em situaçao de check



        if (checkingPiecePosition == null) {
            return false;
        }



        String kingPosition = board.getKingByColor(kingColor);
        char kingColumn = kingPosition.charAt(0);
        int kingRow = Character.getNumericValue(kingPosition.charAt(1));

        // If the moving piece is the king, check if the destination is safe
        if (kingColumn == dx && kingRow == dy) {
            return !board.isPositionAttacked(x, y, kingColor, "");
        }

        // check if we're capturing the checking piece
        char checkingColumn = checkingPiecePosition.charAt(0);
        int checkingRow = Character.getNumericValue(checkingPiecePosition.charAt(1));
        if (x == checkingColumn && y == checkingRow) {
            return true;
        }

        List<String> blockingSquares = getBlockingSquares(kingPosition, checkingPiecePosition);
        blockingSquares.add(checkingPiecePosition);

        for (String blockingSquare : blockingSquares) {
            char squareColumn = blockingSquare.charAt(0);
            int squareRow = Character.getNumericValue(blockingSquare.charAt(1));
            if (squareColumn == x && squareRow == y) {
                return true;
            }
        }


        return false;
    }


    /**
     * Validates if a checkmate / stalemate is on the board for selected king
     * @param kingColor color of king to validate for
     * @return <code>true</code> if there is a checkmate/stalemate
     *         <code>false</code> otherwise
     * */

    private boolean verifyCheckMate(Piece.Color kingColor){
        // verifica se kingColor perdeu
        if (isKingInCheck(kingColor) &&
                isKingStuck(kingColor, null) &&
                !isKingSaveable(kingColor, getCheckPiece(kingColor))) {
            winner = kingColor;
            isgameover = true;

            return true;
        }

        if (!isKingInCheck(kingColor) &&
               !hasAnyValidMoves(kingColor) ) {

            isgameover = true;
            return true;
        }


        return false;
    }

    /**
     * Iterates though all pieces of the given color validating each move. Used to check if a stalemate is in place
     * @param color color of the pieces with moves to validate
     * @return <code>true</code> if there is a possible move
     *         <code>false</code> otherwise
     * */

    private boolean hasAnyValidMoves(Piece.Color color) {
        // Iterate through all pieces of the given color
        for (Object obj : board.getPieceList()) {
            Piece piece = (Piece) obj;
            if (piece.getColor() == color) {
                // Get all possible moves for this piece
                List<String> possibleMoves = piece.getPossibleMoves(board.getPieceList());

                // Check each possible move
                for (String move : possibleMoves) {
                    char destCol = move.charAt(0);
                    int destRow = Character.getNumericValue(move.charAt(1));

                    // If any move is valid, return true
                    if (isMoveValid(piece.getColumn(), piece.getRow(), destCol, destRow)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    /**
     * Exports the current game state directly to a .txt file
     * The file will be named after the current player and contain the board state
     * @return <code>true</code> if save was successful;
     *         <code>false</code> otherwise
     */
    public boolean exportGame() {
        try {
             String Color = isWhiteToMove() ? "white" : "black";
            String fileName = "save_file"+ ".txt";
            String gameState = Color + "," + board.getBoardStringForFile();

            java.io.FileWriter writer = new java.io.FileWriter(fileName);
            writer.write(gameState);
            writer.close();

            ModelLog.getInstance().addLog("Game saved successfully to " + fileName);
            return true;
        } catch (Exception e) {
            ModelLog.getInstance().addLog("Error saving game: " + e.getMessage());
            return false;
        }
    }
    /**
     * Imports a game from a .txt or .csv file
     * The file should be named after a player and contain the board state
     * @param fileName the contents of the file to import
     * @return <code>true</code> if import was successful;
     *         <code>false</code> otherwise
     */
    public boolean importGame(String fileName) {
        if (fileName == null || fileName.isEmpty())
            return false;

        try {
            // Read entire file content as a single string
            String gameText = new String(Files.readAllBytes(Paths.get(fileName))).trim();

            if (gameText.isEmpty())
                return false;

            String[] tokens = gameText.split(",");

            int startIndex = 0;
            if (tokens.length > 0 && (tokens[0].equalsIgnoreCase("white") || tokens[0].equalsIgnoreCase("black"))) {
                startIndex = 1;
            }

            // Clear current board before import
            board.clearBoard();

            for (int i = startIndex; i < tokens.length; i++) {
                String pieceStr = tokens[i].trim();
                if (pieceStr.length() < 3) {
                    return false;
                }

                Piece piece = PieceFactory.createPiece(
                        "" + pieceStr.charAt(0) + pieceStr.charAt(1) + Character.getNumericValue(pieceStr.charAt(2))
                );

                if (piece != null) {
                    board.addPiece(piece);
                } else {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            ModelLog.getInstance().addLog("Error importing game: " + e.getMessage());
            return false;
        }
    }


    /**
     * Saves chessgame state into Memento
     * @return <code>true</code> if save is successfull;
     *         <code>false</code> otherwise
     * */
    public IMemento createMemento() {
        return new ChessGameMemento(this);
    }
    /**
     * restores last saved chessgame state from memento
     * @param memento created class from memento interface
     * */
    public void restoreFromMemento(IMemento memento) {
        if (memento instanceof ChessGameMemento m) {
            setBoardFromString(m.boardState);
            this.whiteToMove = m.whiteToMove;
            this.ischeck = m.ischeck;
            this.isgameover = m.isgameover;
            this.isPromotion = m.isPromotion;
            this.winner = m.winner;
            this.player1 = m.player1;
            this.player2 = m.player2;
            this.lastTwoSquarePawnMove = m.lastTwoSquarePawnMove;
            this.pieceChoice = m.promotionChoice;
        } else {
            ModelLog.getInstance().addLog("Invalid memento");
        }
    }
    /**
     * Retrieves the current color choice used for placing a piece in editor mode.
     *
     * @return A string representing the selected color, typically "WHITE" or "BLACK".
     */
    public boolean getColorChoice() {
        return colorChoice;
    }

    /**
     * Retrieves the current piece choice selected in editor mode.
     *
     * @return The character representing the selected piece (e.g., 'k' for king, 'q' for queen).
     */

    public char getPieceChoice() {
        return pieceChoice;
    }

    /**
     * A memento class that captures and stores the internal state of a ChessGame
     * so that it can be restored later. This is part of the implementation of the Memento
     * design pattern.
     */
    private static class ChessGameMemento implements IMemento {
        private final String boardState;
        private final boolean whiteToMove;
        private final boolean ischeck;
        private final boolean isgameover;
        private final boolean isPromotion;
        private final Piece.Color winner;
        private final String player1;
        private final String player2;
        private final String lastTwoSquarePawnMove;
        private final char promotionChoice;

        /**
         * Creates a new memento by capturing the current state of the given ChessGame.
         *
         * @param game the chessgame whose state to save
         */

        private ChessGameMemento(ChessGame game) {
            this.boardState = game.getCurrentBoard();
            this.whiteToMove = game.isWhiteToMove();
            this.ischeck = game.ischeck;
            this.isgameover = game.isgameover;
            this.isPromotion = game.isPromotion;
            this.winner = game.winner;
            this.player1 = game.player1;
            this.player2 = game.player2;
            this.lastTwoSquarePawnMove = game.lastTwoSquarePawnMove;
            this.promotionChoice = game.pieceChoice;
        }
    }

    /**
     * Creates a new board from an existing one creating new instances for every piece
     * @param boardString ArrayList of the pieces of a board to copy
     *
     * */


    private void setBoardFromString(String boardString) {
        String[] pieceStrings = boardString.split(",");
        Board newBoard = new Board();

        for (String pieceStr : pieceStrings) {
            if (pieceStr.isBlank()) continue;
            Piece piece = PieceFactory.createPiece(pieceStr.trim());
            newBoard.addPiece(piece);
        }
        this.board = newBoard;
    }


    /**
     * Returns a string representation of the piece at the specified board coordinates.
     *
     * @param x the column of the piece,a character from 'a' to board size
     * @param y the row of the piece,an integer from 1 to board size
     * @return <code>String</code> if there is a piece at position, its String
     *         <code>null</code> if there is not a piece at given position
     * */


    public String getPieceString(char x,int y){
        return board.seePieceAt(x, y);
    }


    /**
     * Returns the list of valid moves of the piece at given location
     *
     * @param x the column of the piece,a character from 'a' to board size
     * @param y the row of the piece,an integer from 1 to board size
     * @return ListString of the given piece validated moves, empty if none
     * */

    public List<String> getPieceMoves(char x, int y){
        List<String> validMoves = new ArrayList<>();
        List<String> possibleMoves = board.getPieceMoveList(x, y);

        for (String move : possibleMoves) {

            if (!isMoveValid(x, y, move.charAt(0), Character.getNumericValue(move.charAt(1)))) {
                continue;
            }
            validMoves.add(move);
        }

        return validMoves;
    }
    /**
     * Recieves the users choice for a piece type
     * @param choice character representation of the promoted piece q/n/r/b
     * */

    public void setPieceChoice(char choice) {
        this.pieceChoice = choice;
    }
    /**
     * swaps the users choice for a piece color
     *
     * */
    public void setColorChoice(){
        this.colorChoice = !colorChoice;
    }
    /**
     * Promotes pawn at given location to the users choice, removes the pawn and places new choice
     * @param color color of pawn to promote as a string
     * @param dx original Column of piece to promote
     * @param dy original row of piece to promote
     *
     * */
    public void pawnPromotion(String color, char dx, int dy){
        char type;
        if(color.equals("WHITE")){
            type = Character.toUpperCase(pieceChoice);
        } else{
            type = Character.toLowerCase(pieceChoice);
        }

        board.removePiece(dx, dy);
        Piece promotedPiece = PieceFactory.createPiece("" + type + dx + dy);
        if(promotedPiece != null){
            board.addPiece(promotedPiece);
        }

        isPromotion = false;
    }
    /**
     * Completes the promotion checking if it generates a checkmate
     * */
    public void completePromotion() {
        String boardState = board.getBoardString();
        String[] pieces = boardState.split(",");

        String pawnPos = null;
        for (String piece : pieces) {
            if (piece == null) continue;

            char type = piece.charAt(0);
            int row = Character.getNumericValue(piece.charAt(2));

            if ((type == 'P' && row == 8) || (type == 'p' && row == 1)) {
                pawnPos = piece;
                break;
            }
        }

        if (pawnPos != null) {
            char col = pawnPos.charAt(1);
            int row = Character.getNumericValue(pawnPos.charAt(2));
            pawnPromotion(whiteToMove ? "WHITE" : "BLACK", col, row);
            togglePlayer();
            ModelLog.getInstance().addLog("Pawn promoted!");

            // Check for checkmate after promotion
            boolean isGameOver = verifyCheckMate(whiteToMove ? Piece.Color.BLACK : Piece.Color.WHITE);
            if (isGameOver) {
                isgameover = true;
            }
        }
    }

    /**
     * Checks whether a pawn is currently being promoted.
     *
     * @return <code>true</code> if a pawn promotion is in progress;
     *         <code>false</code> otherwise
     */

    public boolean isPromoting(){
        return isPromotion;
    }

    /**
     * Checks if the game is currently in editing mode.
     *
     * @return {@code true} if the game is in editor mode; {@code false} otherwise.
     */
    public boolean isEditing(){ return isEditing; }

    /**
     * Sets the editing mode of the game. When set to {@code true}, the board is prepared for editing
     * by resetting game state variables such as player turn, check status, promotion status, and winner.
     *
     * @param value {@code true} to enable editing mode; {@code false} to disable it.
     */
    public void setIsEditing(boolean value){
        isEditing = value;
        if (value) {
            whiteToMove = true;
            ischeck = false;
            isgameover = false;
            isPromotion = false;
            winner = null;
            lastTwoSquarePawnMove = null;
            pieceChoice = '\0';
            colorChoice = true;
        }
    }


    /**
     * Adds a piece to the board in editor mode at the specified destination.
     * <p>
     * If a piece already exists at the destination square, it is removed.
     * If the selected piece is a king and a king of the selected color already exists on the board,
     * the method returns without making changes (to enforce only one king per color).
     * The method creates the piece using the current piece and color choices,
     * places it on the board, and then resets the piece and color choices.
     * </p>
     *
     * @param destColChar The column character (e.g., 'A', 'B', ..., 'H') for the destination square.
     * @param destRowNumber The row number (e.g., 1 to 8) for the destination square.
     */
    public void editorAddPiece(char destColChar, int destRowNumber){
        String Color = isWhiteToMove() ? "white" : "black";
        if(pieceChoice == 'k' && board.hasOneKing(Color)){
            return;
        }

        if(board.isOccupied("" + destColChar + destRowNumber)){
            board.removePiece(destColChar, destRowNumber);
        }

        if(colorChoice){
            board.addPiece(PieceFactory.createPiece("" + Character.toUpperCase(pieceChoice) + destColChar + destRowNumber));
            setPieceChoice('\0');
        } else if (!colorChoice){
            board.addPiece(PieceFactory.createPiece("" + Character.toLowerCase(pieceChoice) + destColChar + destRowNumber));
            setPieceChoice('\0');
        }
    }
    public void setPlayer1(String name){
        player1 = name;
    }
    public void setPlayer2(String name){
        player2 = name;

    }
}



