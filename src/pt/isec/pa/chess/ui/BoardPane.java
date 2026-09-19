package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import pt.isec.pa.chess.model.ChessGameManager;

import pt.isec.pa.chess.model.ModelLog;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.res.SoundManager;


import java.util.List;


/**
 * A custom JavaFX Canvas that represents the chess board UI component.
 * It manages the board state display, user interactions, and game modes such as
 * sound, learning mode, and promotion.
 */
public class BoardPane extends Canvas {
    ChessGameManager chessGameManager;
    private int originCol, originRow;
    private boolean isSoundOn;
    private boolean isFirstClick;
    private boolean isGameRunning;

    private boolean isLearning;
    private boolean isPromoting = false;


    /**
     * Initializes a new {@code BoardPane}, which is a custom canvas for rendering the chessboard UI.
     * <p>
     * Sets up default interaction states, dimensions, and listeners for size changes.
     * Also registers event handlers for user interaction and triggers an initial board update.
     *
     * @param chessGameManager the {@code ChessGameManager} instance responsible for managing game logic and state
     */


    public BoardPane(ChessGameManager chessGameManager) {
        this.chessGameManager = chessGameManager;
        this.isFirstClick = true;
        this.isGameRunning = false;
        this.isSoundOn = false;
        setHeight(600);
        setWidth(600);

        widthProperty().addListener((obs, oldVal, newVal) -> update());
        heightProperty().addListener((obs, oldVal, newVal) -> update());


        registerHandlers();
        update();
    }

    private void toggleisPromoting(){
        isPromoting = !isPromoting;
    }


    private void update() {
        if (isGameRunning) {
            drawCheckerboard();
            drawPieces();


        }
    }

    void registerHandlers() {
        super.setOnMouseClicked(event -> handleCanvasClick(event.getX(), event.getY()));

        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_GAME_STARTED,
                evt -> {

                    setGameRunning(true);

                });

        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_BOARD_STATE,
                evt -> {
                    update();
                });

        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_PROMOTION,
                evt -> {
                    toggleisPromoting();
                });


    }


    private void handleCanvasClick(double x, double y) {
        double squareWidth = super.getWidth() / chessGameManager.getBoardSize();
        double squareHeight = super.getHeight() / chessGameManager.getBoardSize();

        int colIndex = (int) (x / squareWidth);
        int rowIndex = (int) (y / squareHeight);

        drawCheckerboard(); // to remove any old highlights at each click
        drawPieces();


        if(chessGameManager.isEditing()){
            char pieceChoice = chessGameManager.getPieceChoice();
            if (pieceChoice == '\0') {
                return;
            }
            char destColChar = (char) ('a' + colIndex);
            int destRowNumber = chessGameManager.getBoardSize() - rowIndex;

            chessGameManager.editorAddPiece(destColChar, destRowNumber);
            drawPieces();
            ModelLog.getInstance().addLog("Placed piece: " + pieceChoice + " at " + destColChar + destRowNumber);
        }


        if (!isPromoting) {
            if (isFirstClick) {
                // o primeiro click seleciona a peça
                originCol = colIndex;
                originRow = rowIndex;


                char originColumnChar = (char) ('a' + originCol);
                int originRowNumber = chessGameManager.getBoardSize() - originRow;



                String PlayerColor = chessGameManager.getWhitetoMove() ? "WHITE" : "BLACK";


                if (chessGameManager.getPieceColor(originColumnChar, originRowNumber) != null && !chessGameManager.getPieceColor(originColumnChar, originRowNumber).equals(PlayerColor)) {
                    return;
                }

                if (isLearning) {
                    List<String> pieceMoveList = chessGameManager.getPieceMoveList(originColumnChar, originRowNumber);
                    if (!pieceMoveList.isEmpty()) {
                        for (String move : pieceMoveList) {
                            paintSquare(move, "red");
                        }
                    }
                }

                if (chessGameManager.isPositionOccupied("" + originColumnChar + originRowNumber)) {
                    isFirstClick = false;
                    paintSquare("" + originColumnChar + originRowNumber, "gray");
                    System.out.println("peça selecionada: " + originColumnChar + originRowNumber);
                }
            } else {
                // segundo click é o destino
                int destCol = colIndex;
                int destRow = rowIndex;

                char originColumnChar = (char) ('a' + originCol);
                int originRowNumber = chessGameManager.getBoardSize() - originRow;

                char destColumnChar = (char) ('a' + destCol);
                int destRowNumber = chessGameManager.getBoardSize() - destRow;


                String destPieceinfo = null;
                String originPieceinfo;

                List<String> pieceMoveList = chessGameManager.getPieceMoveList(originColumnChar, originRowNumber);
                if (!pieceMoveList.isEmpty()) {
                    for (String move : pieceMoveList) {
                        // verifica se o click é um move valido
                        if (move.charAt(0) == destColumnChar && Character.getNumericValue(move.charAt(1)) == destRowNumber) {


                            //guardar info do destino para sons
                            originPieceinfo = chessGameManager.getPieceString(originColumnChar, originRowNumber);

                            if (chessGameManager.isPositionOccupied("" + destColumnChar + destRowNumber)) {
                                destPieceinfo = chessGameManager.getPieceString(destColumnChar, destRowNumber);
                            }


                            if (chessGameManager.makeMove(originColumnChar, originRowNumber, destColumnChar, destRowNumber)) {
                                if (isSoundOn)
                                    playSoundMove(originPieceinfo, destPieceinfo, destColumnChar, destRowNumber);
                            }

                            isFirstClick = true;
                            return;

                        }
                    }

                }
                isFirstClick = true;
            }
        }
    }

    private void drawCheckerboard() {
        GraphicsContext gc = super.getGraphicsContext2D();
        double width = super.getWidth();
        double height = super.getHeight();
        double squareWidth = width / chessGameManager.getBoardSize();
        double squareHeight = height / chessGameManager.getBoardSize();

        for (int row = 0; row < chessGameManager.getBoardSize(); row++) {
            for (int col = 0; col < chessGameManager.getBoardSize(); col++) {

                boolean isLight = (row + col) % 2 == 0;
                gc.setFill(isLight ? Color.WHITE : Color.LIGHTBLUE);
                gc.fillRect(col * squareWidth, row * squareHeight, squareWidth, squareHeight);

            }
        }
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.setFill(Color.BLACK);
    }

    private void drawPieces() {

        String boardString = chessGameManager.getBoardString();
        if (boardString.isEmpty()) return;
        

        GraphicsContext gc = super.getGraphicsContext2D();
        double width = super.getWidth();
        double height = super.getHeight();
        double squareWidth = width / chessGameManager.getBoardSize();
        double squareHeight = height / chessGameManager.getBoardSize();

        String info[] = boardString.split(",");


        for (String part : info) {
            char type = part.charAt(0);
            char col = part.charAt(1);
            int row = Character.getNumericValue(part.charAt(2));

            int colIndex = col - 'a'; // a -> 0, b -> 1, ...
            int rowIndex = chessGameManager.getBoardSize() - row; // 8 -> 0 (top), 1 -> 7 (bottom)

            String imageName = getImageName(type);
            Image image = ImageManager.getImage(imageName);

            if (image != null) {
                gc.drawImage(image, colIndex * squareWidth, rowIndex * squareHeight, squareWidth, squareHeight);
            } else {
                System.out.println("Image not found for: " + imageName);
            }
        }
    }



    private void paintSquare(String move, String color) {

        GraphicsContext gc = super.getGraphicsContext2D();
        double width = super.getWidth();
        double height = super.getHeight();
        double squareWidth = width / chessGameManager.getBoardSize();
        double squareHeight = height / chessGameManager.getBoardSize();

        char col = move.charAt(0);
        int row = Character.getNumericValue(move.charAt(1));

        int colIndex = col - 'a';
        int rowIndex = chessGameManager.getBoardSize() - row;


        if (color.equals("red")) {
            gc.setFill(Color.INDIANRED);
            gc.fillRect(colIndex * squareWidth, rowIndex * squareHeight, squareWidth, squareHeight);
        } else if(color.equals("gray")){
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(colIndex * squareWidth, rowIndex * squareHeight, squareWidth, squareHeight);
            drawPieces();
        }
        else {

            boolean isLight = (row + col) % 2 == 0;
            gc.setFill(isLight ? Color.WHITE : Color.LIGHTBLUE);
            gc.fillRect(col * squareWidth, row * squareHeight, squareWidth, squareHeight);
        }
    }

    private String getImageName(char type) {
        return switch (type) {
            case 'b' -> "bishopB.png";
            case 'B' -> "bishopW.png";
            case 'n' -> "knightB.png";
            case 'N' -> "knightW.png";
            case 'q' -> "queenB.png";
            case 'Q' -> "queenW.png";
            case 'k' -> "kingB.png";
            case 'K' -> "kingW.png";
            case 'p' -> "pawnB.png";
            case 'P' -> "pawnW.png";
            case 'r' -> "rookB.png";
            case 'R' -> "rookW.png";
            default -> null;
        };
    }

    private String getPieceName(char type) {
        return switch (type) {
            case 'b', 'B' -> "bishop";
            case 'n', 'N' -> "knight";
            case 'q', 'Q' -> "queen";
            case 'k', 'K' -> "king";
            case 'p', 'P' -> "pawn";
            case 'r', 'R' -> "rook";
            default -> null;
        };
    }

    private void playSoundMove(String originPiece, String destPiece, char destColumn, int destRow) {
        char originPieceType = originPiece.charAt(0);
        char originPieceCol = originPiece.charAt(1);
        int originPieceRow = Character.getNumericValue(originPiece.charAt(2));


        String PlayerColor = chessGameManager.getWhitetoMove() ? "black" : "white";


        if (destPiece != null) {
            char destPieceType = destPiece.charAt(0);
            char destPieceCol = destPiece.charAt(1);
            int destPieceRow = Character.getNumericValue(destPiece.charAt(2));
            String DestPieceColor = chessGameManager.getWhitetoMove() ? "white" : "black";

            SoundManager.playSequence(PlayerColor, getPieceName(originPieceType), "" + originPieceCol, "" + originPieceRow, "" + DestPieceColor, getPieceName(destPieceType), "" + destPieceCol, "" + destPieceRow);

        } else {
            SoundManager.playSequence(PlayerColor, getPieceName(originPieceType), "" + originPieceCol, "" + originPieceRow, "" + destColumn, "" + destRow);
        }

    }

    /**
     * Checks whether the sound is currently enabled on the board.
     *
     * @return true if sound is enabled, false otherwise.
     */

    public boolean isSoundOn() {
        return isSoundOn;
    }


    /**
     * Toggles the sound setting for the board.
     * <p>
     * If sound is currently enabled, this method disables it; if it is disabled, this method enables it.
     */


    public void toggleSound() {
        isSoundOn = !isSoundOn;
    }


    /**
     * Toggles the state of the game running flag.
     * <p>
     * If the game is currently running, it will be set to not running, and vice versa.
     */

    public void toggleIsGameRunning() {
        isGameRunning = !isGameRunning;
    }


    /**
     * Sets the running state of the game.
     *
     * @param gameRunning {@code true} to indicate that the game is currently running,
     *                    {@code false} to indicate that it is not.
     */
    public void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    /**
     * Checks whether the game is currently running.
     *
     * @return {@code true} if the game is in progress, {@code false} otherwise.
     */

    public boolean isGameRunning() {
        return isGameRunning;
    }

    /**
     * Toggles the learning mode state.
     * <p>If learning mode is currently enabled, it will be disabled, and vice versa.</p>
     */
    public void toggleIsLearning(){
        isLearning = !isLearning;
    }

    /**
     * Checks if the board is currently in learning mode.
     *
     * @return true if learning mode is active, false otherwise.
     */
    public boolean isLearning(){
        return isLearning;
    }
}

