package pt.isec.pa.chess.model;


import pt.isec.pa.chess.model.caretaker.Caretaker;
import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.memento.IMemento;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Facade class for interaction with the data model
 * <p>has use of Property change for async updates</p>
 *
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 * */


public class ChessGameManager {

    /** Property name indicating that a new game has started. */
    public static final String PROP_GAME_STARTED = "gameStarted";


    /** Property name representing the updated state of the board. */

    public static final String PROP_BOARD_STATE = "boardState";

    /** Property name indicating a change in the current player. */
    public static final String PROP_CURRENT_PLAYER = "currentPlayer";


    /** Property name indicating that the game is over. */
    public static final String PROP_GAME_OVER = "gameOver";

    /** Property name triggered when a pawn promotion occurs. */
    public static final String PROP_PROMOTION = "promotion";
    /** variable to save the data model*/

    private ChessGame chessGame;
    /**
     * Handles property change listeners and fires change events
     * to notify observers (e.g., UI components or controllers).
     */
    private final PropertyChangeSupport support;


    /**
     * Manages the undo and redo history using the Memento design pattern.
     */
    private final Caretaker caretaker;

    /**
     * Constructor of this class creating the instances of chessgame, support, and caretaker
     * */

    public ChessGameManager(){
        chessGame = new ChessGame();
        support = new PropertyChangeSupport(this);
        caretaker = new Caretaker();
    }
    /**
     *
     * */
    private final static int boardSize = 8;



    /**
     * Returns whether it is White's turn to move.
     *
     * @return true if it's White's turn, false otherwise.
     */
    public boolean getWhitetoMove(){
        return chessGame.isWhiteToMove();
    }

    /**
     * Retrieves the current state of the chess board as a string representation.
     *
     * @return a string representing the current board configuration
     */

    public String getBoardString(){
        return chessGame.getCurrentBoard();
    }

    /**
     * Clears the current chess board by removing all pieces.
     */
    public void clearBoard(){
        chessGame.clearBoard();
    }

    /**
     * Returns the size of the chess board (number of squares per side).
     *
     * @return the board size as an integer
     */
    public int getBoardSize(){
        return boardSize;
    }

    /**
     * Starts a new chess game with the given player names.
     * Clears the undo/redo history.
     * Fires property change events to notify listeners about:
     * - Game start
     * - Board state update
     * - Current player update
     * - Game over status
     *
     * @param p1 Name of player 1
     * @param p2 Name of player 2
     */
    public void start( String p1, String p2){
        caretaker.clearHistory();
        chessGame.start(p1,p2);
        support.firePropertyChange(PROP_GAME_STARTED, null, true);
        support.firePropertyChange(PROP_BOARD_STATE, null, getBoardString());
        support.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayer());
        support.firePropertyChange(PROP_GAME_OVER, null, true);
    }

    /**
     * Loads a game from the specified file.
     * If loading is successful, fires property change events to update listeners about:
     * - Game start
     * - Board state update
     * - Current player update
     *
     * @param file The filename from which to load the game
     * @return true if the game was loaded successfully, false otherwise
     */
    public boolean loadGame(String file){
        boolean success = chessGame.loadGame(file);
        if (success) {
            support.firePropertyChange(PROP_GAME_STARTED, null, true);
            support.firePropertyChange(PROP_BOARD_STATE, null, getBoardString());
            support.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayer());
        }
        return success;
    }

    /**
     * Saves the current game state to a file.
     *
     * @param file The filename where the game should be saved
     */
    public void saveGame(String file){
        chessGame.saveGame();
    }


    /**
     * Imports a game from the specified file and updates the game state.
     *
     * @param file The filename from which the game will be imported
     */
    public boolean importGame(String file){
        boolean success = chessGame.importGame(file);
        support.firePropertyChange(PROP_GAME_STARTED, null, true);
        support.firePropertyChange(PROP_BOARD_STATE, null, getBoardString());
        support.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayer());
        return success;

    }

    /**
     * Exports the current game state by delegating to the underlying chess game logic.
     */
    public void exportGame(){
        chessGame.exportGame();

    }


    /**
     * Attempts to make a move on the chessboard from the source position (dx, dy)
     * to the destination position (x, y). This method handles move validation,
     * updating game state, promotion handling, and firing property change events
     * to notify observers of the state changes.
     *
     * @param dx the column of the piece to move
     * @param dy the row of the piece to move
     * @param x the target column for the move
     * @param y the target row for the move
     * @return true if the move was successfully made; false otherwise
     */
    public boolean makeMove(char dx, int dy, char x, int y){
        if (isCheckMate() || chessGame.isPromoting()) {
            return false;
        }

        String oldBoard = getBoardString();
        String oldPlayer = getCurrentPlayer();

        IMemento currentMemento = chessGame.createMemento();
        caretaker.saveState(currentMemento);


        boolean success = chessGame.makeMove(dx, dy, x, y);

        if (success) {
            support.firePropertyChange(PROP_BOARD_STATE, oldBoard, getBoardString());

            if(chessGame.isPromoting()){
                support.firePropertyChange(PROP_PROMOTION, false, true);
            }
            if (!isCheckMate()) {
                support.firePropertyChange(PROP_CURRENT_PLAYER, oldPlayer, getCurrentPlayer());
            }
            ModelLog.getInstance().addLog("Move " + dx + dy + " -> " + x + y);
        }else {
            caretaker.undo(currentMemento); // failed memento
        }

        if (chessGame.isGameOverBool()){
            support.firePropertyChange(PROP_GAME_OVER, null, chessGame.isGameOver());

        }

        return success;
    }

    /**
     * Checks whether the game is over.
     *
     * @return true if the game has ended; false otherwise.
     */
    public boolean isGameOver(){
        return chessGame.isGameOverBool();
    }

    /**
     * Undoes the last move if possible, restoring the previous game state.
     *
     * @return true if the undo was successful; false if there is no move to undo.
     */
    public boolean undo() {
        if (!hasUndo())
            return false;

        IMemento currentState = chessGame.createMemento();
        IMemento prevState = caretaker.undo(currentState);

        if (prevState != null) {
            chessGame.restoreFromMemento(prevState);
            support.firePropertyChange(PROP_BOARD_STATE, null, getBoardString());
            support.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayer());
        }
        return true;
    }

    /**
     * Redoes the previously undone move if possible, restoring the next game state.
     *
     * @return true if the redo was successful; false if there is no move to redo.
     */
    public boolean redo() {
        if (!hasRedo())
            return false;

        IMemento currentState = chessGame.createMemento();
        IMemento nextState = caretaker.redo(currentState);

        if (nextState != null) {
            chessGame.restoreFromMemento(nextState);
            support.firePropertyChange(PROP_BOARD_STATE, null, getBoardString());
            support.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayer());
        }

        return true;
    }

    /**
     * Checks if there is a move available to undo.
     *
     * @return true if undo is possible; false otherwise.
     */
    public boolean hasUndo() {
        return caretaker.canUndo();
    }

    /**
     * Checks if there is a move available to redo.
     *
     * @return true if redo is possible; false otherwise.
     */
    public boolean hasRedo() {
        return caretaker.canRedo();
    }

    /**
     * Gets the current player whose turn it is to move.
     *
     * @return the current player's color as a String (e.g., "WHITE" or "BLACK").
     */

    public String getCurrentPlayer(){
        return chessGame.getCurrentPlayer();
    }

    /**
     * Gets the player who is not currently taking their turn.
     *
     * @return the other player's color as a String (e.g., "WHITE" or "BLACK").
     */
    public String getOtherPlayer(){
        return chessGame.getOtherPlayer();
    }

    /**
     * Checks if a given board position is currently occupied by any piece.
     *
     * @param position the board position in standard notation (e.g., "e4").
     * @return true if the position is occupied, false otherwise.
     */
    public boolean isPositionOccupied(String position){
        return chessGame.isPositionOccupied(position);
    }


    /**
     * Adds a {@link PropertyChangeListener} for a specific property.
     *
     * @param property the name of the property to listen for changes on
     * @param listener the listener to be added and notified when the property changes
     */

    public void addPropertyChangeListener(String property,PropertyChangeListener listener) {
        support.addPropertyChangeListener(property,listener);
    }


    /**
     * Retrieves the list of valid moves for the piece located at the specified position.
     *
     * @param x the column character of the piece's position (e.g., 'a' through 'h').
     * @param y the row number of the piece's position (e.g., 1 through 8).
     * @return a list of strings representing valid move positions for the piece.
     */

    public List<String> getPieceMoveList(char x, int y){
        return chessGame.getPieceMoves(x, y);
    }


    /**
     * Returns the string representation of the piece located at the specified position.
     *
     * @param x the column character of the piece's position (e.g., 'a' through 'h').
     * @param y the row number of the piece's position (e.g., 1 through 8).
     * @return a string representing the piece at the given position, or null/empty if no piece is present.
     */
    public String getPieceString(char x,int y){
        return chessGame.getPieceString(x, y);
    }

    /**
     * Returns the color of the piece at the specified position on the board.
     *
     * @param x the column character of the piece's position (e.g., 'a' through 'h').
     * @param y the row number of the piece's position (e.g., 1 through 8).
     * @return the color of the piece ("WHITE" or "BLACK"), or null if no piece is present at the position.
     */
    public String getPieceColor(char x, int y){
        return chessGame.getPieceColor(x, y);
    }

    /**
     * Returns the ending state of the game.
     *
     * @return a string describing the game over state, such as the winner or draw status.
     */
    public String getEndingState(){
        return chessGame.isGameOver();
    }

    /**
     * Checks if the game is currently in a checkmate state.
     *
     * @return true if the game is over due to checkmate; false otherwise.
     */
    public boolean isCheckMate(){
        return chessGame.isGameOverBool();
    }

    /**
     * Sets the currently selected piece choice in the game and logs the selection.
     *
     * @param choice the character representing the chosen piece (e.g., 'k' for king).
     */
    public void setPieceChoice(char choice){
        chessGame.setPieceChoice(choice);
        ModelLog.getInstance().addLog("Choice: " + choice);
    }

    /**
     * Returns the currently selected piece choice.
     *
     * @return the character representing the chosen piece (e.g., 'k' for king), or '\0' if none is selected.
     */
    public char getPieceChoice(){
        return chessGame.getPieceChoice();
    }

    /**
     * Sets the current color choice for piece placement or editing.
     *
     * @param color the color to set, typically "WHITE" or "BLACK".
     */
    public void setColorChoice(){
        chessGame.setColorChoice();
    }

    /**
     * Returns the currently selected color choice for piece placement or editing.
     *
     * @return the color choice as a String (e.g., "WHITE" or "BLACK")
     */
    public String getColorChoice(){
        return chessGame.getColorChoice() ? "WHITE" : "Black";
    }

    /**
     * Checks if the game is currently in editing mode.
     *
     * @return true if editing mode is active, false otherwise.
     */
    public boolean isEditing(){
        return chessGame.isEditing();
    }

    /**
     * Enables or disables the editing mode.
     * When enabled, updates the board state, current player, and game over properties.
     *
     * @param value true to enable editing mode, false to disable it.
     */
    public void setIsEditing(boolean value){
        if (value) {
            chessGame.setIsEditing(true);

            support.firePropertyChange(PROP_BOARD_STATE, null, getBoardString());
            support.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayer());
            support.firePropertyChange(PROP_GAME_OVER, null, false);
        } else {
            chessGame.setIsEditing(false);
        }
    }

    /**
     * Completes the promotion of a pawn to the chosen piece.
     * Updates the game state and notifies listeners about changes
     * in promotion status, board state, and current player.
     */
    public void completePromotion() {
        String oldBoard = getBoardString();
        String oldPlayer = getCurrentPlayer();

        chessGame.completePromotion();

        support.firePropertyChange(PROP_PROMOTION, true, false);
        support.firePropertyChange(PROP_BOARD_STATE, oldBoard, getBoardString());
        support.firePropertyChange(PROP_CURRENT_PLAYER, oldPlayer, getCurrentPlayer());
    }

    /**
     * Adds a piece to the board at the specified column and row in editor mode.
     * Delegates the action to the underlying chess game logic.
     *
     * @param destColChar The column character where the piece is to be placed.
     * @param destRowNumber The row number where the piece is to be placed.
     */
    public void editorAddPiece(char destColChar, int destRowNumber){
        chessGame.editorAddPiece(destColChar, destRowNumber);
    }
    public void setPlayer1(String name){
        chessGame.setPlayer1(name);
    }
    public void setPlayer2(String name){
        chessGame.setPlayer2(name);

    }
}


