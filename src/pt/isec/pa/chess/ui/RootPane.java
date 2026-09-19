package pt.isec.pa.chess.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Optional;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

import pt.isec.pa.chess.model.data.ChessGame;
import pt.isec.pa.chess.model.data.board.Board;

import pt.isec.pa.chess.model.data.pieces.Piece;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.res.SoundManager;

/**
 *
 * Main UI container for the chess game application.
 * Initializes and manages the layout, including the chess board,
 * player status, game menus, and promotion interface.
 *
 * <p>This class binds the user interface to the game logic via the
 * ChessGameManager, updates the display in response to game state changes,
 * and handles user actions such as starting a new game, saving/loading,
 * toggling game modes (normal/learning), and managing pawn promotion.
 *
 *
 * @see Board
 * @see Piece
 * @see ChessGameManager
 * @see ChessGame
 * @see ImageManager
 * @see SoundManager
 * @see ModelLog
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 * */


import java.util.Optional;

public class RootPane extends BorderPane {
    /** Manages the overall state and logic of the chess game. */
    ChessGameManager chessGameManager;
    /** Main menu bar for the application. */
    MenuBar menuBar;
    /** Label to display the current player's turn. */
    private Label currentPlayerLabel;
    /** Label to display the winner when the game ends. */
    private Label winnerLabel;
    /** Container for right-side UI elements (e.g., player info, promotion). */
    Pane right;
    /** Canvas for additional drawing (currently unused). */
    Canvas canvas;
    /** Container for the center region of the UI, holds the board. */
    BorderPane center;
    /** Custom pane for rendering and interacting with the chess board. */
    BoardPane boardPane;

    /** "Game" menu with options like new, load, save, etc. */
    Menu gameMenu;
    /** "Mode" menu for switching between normal and learning modes. */
    Menu modeMenu;
    /** Menu item to activate the editor */
    MenuItem editorItem;
    /** Menu item to start a new game. */
    MenuItem newItem;
    /** Menu item to load a saved game. */
    MenuItem loadItem;
    /** Menu item to save the current game. */
    MenuItem saveItem;
    /** Menu item to import a game. */
    MenuItem importItem;
    /** Menu item to export the current game. */
    MenuItem exportItem;
    /** Menu item to quit the application. */
    MenuItem quitItem;
    /** Menu item to switch to normal mode. */
    MenuItem normalItem;
    /** Menu item to switch to learning mode. */
    MenuItem learningItem;
    /** Menu item to undo the last move (in learning mode). */
    MenuItem undoItem;
    /** Menu item to redo the last undone move (in learning mode). */
    MenuItem redoItem;
    /** Temporary variable to capture user input. */
    String input;
    /** Toggle button to enable or disable game sound. */
    ToggleButton soundButton;
    /** Button for selecting queen as promotion piece. */
    Button queenButton;
    /** Button for selecting bishop as promotion piece. */
    Button bishopButton;
    /** Button for selecting knight as promotion piece. */
    Button knightButton;
    /** Button for selecting rook as promotion piece. */
    Button rookButton;
    /** Button to add a pawn in editor mode */
    Button addPawn;
    /** Button to add a king in editor mode */
    Button addKing;
    /** Button to add a queen in editor mode */
    Button addQueen;
    /** Button to add a bishop in editor mode */
    Button addBishop;
    /** Button to add a knight in editor mode */
    Button addKnight;
    /** Button to add a rook in editor mode */
    Button addRook;
    /** Container for displaying pawn promotion options. */
    Button colorButton;
    VBox promotionBox;
    /** Container for displaying the editor mode options */
    VBox editorBox;

    /**
     * Constructs the main UI container for the chess application.
     *
     * @param size The preferred size (in pixels) for the chess board area.
     * @param chessGameManager The game manager responsible for handling game logic and state.
     *
     * <p>This constructor initializes the layout by creating all necessary UI components,
     * registers event handlers for user actions and game state changes, and performs an initial UI update.
     */

    public RootPane(double size, ChessGameManager chessGameManager){
        this.chessGameManager = chessGameManager;

        createViews(size);
        registerHandlers();
        update();
    }



    /**
     * Initializes and constructs all UI components for the chess game's main view.
     *
     * <p>This includes setting up:
     * <ul>
     *   <li>The right-side panel with current player info, sound toggle, and promotion options.</li>
     *   <li>The menu bar with game controls.</li>
     *   <li>The central GridPane containing the chess board and coordinate labels.</li>
     * </ul>
     *
     * <p>The layout is responsive and adjusts the board size based on window dimensions.
     *
     * @param size The preferred pixel size for the chess board area.
     */

    private void createViews(double size) {
        right = new VBox(20);
        currentPlayerLabel = new Label();
        currentPlayerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        soundButton = new ToggleButton("\uD83D\uDD07");

        createPromotionBox();
        createEditorBox();


        VBox box = new VBox(currentPlayerLabel, soundButton);
        box.setSpacing(10);
        box.setPrefWidth(200);
        box.setAlignment(Pos.TOP_CENTER); // center items horizontally

        right.getChildren().addAll(box, promotionBox, editorBox);

        this.menuBar = createMenu();
        this.setTop(menuBar);
        this.setRight(right);

        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #808080;");
        grid.setAlignment(Pos.CENTER);

        boardPane = new BoardPane(chessGameManager);
        int boardSize = chessGameManager.getBoardSize();
        double squareSize = size / boardSize;
        boardPane.setWidth(size);
        boardPane.setHeight(size);

        for (int row = 0; row < boardSize; row++) {
            int rank = boardSize - row;
            Label leftLabel = createLabel(String.valueOf(rank), squareSize);
            Label rightLabel = createLabel(String.valueOf(rank), squareSize);
            grid.add(leftLabel, 0, row + 1); // left
            grid.add(rightLabel, boardSize + 1, row + 1); // right
        }

        for (int col = 0; col < boardSize; col++) {
            char file = (char) ('A' + col);
            Label topLabel = createLabel(String.valueOf(file), squareSize);
            Label bottomLabel = createLabel(String.valueOf(file), squareSize);
            grid.add(topLabel, col + 1, 0); // top
            grid.add(bottomLabel, col + 1, boardSize + 1); // bottom
        }

        grid.add(boardPane, 1, 1, boardSize, boardSize);

        grid.widthProperty().addListener((obs, oldVal, newVal) -> {
            resizeBoard(grid);
        });
        grid.heightProperty().addListener((obs, oldVal, newVal) -> {
            resizeBoard(grid);
        });

        this.setCenter(grid);
    }

    /**
     * Dynamically resizes the chess board and coordinate labels based on the available space
     * in the provided {@link GridPane}.
     *
     * <p>This method ensures that the board remains square and all labels are resized proportionally
     * when the window is resized. It calculates the optimal square size to fit within the layout,
     * accounting for label columns and rows.
     *
     * @param grid The GridPane containing the board and coordinate labels.
     */
    private void resizeBoard(GridPane grid) {
        int boardSize = chessGameManager.getBoardSize();

        double totalWidth = grid.getWidth();
        double totalHeight = grid.getHeight();

        double availableWidth = totalWidth / (boardSize + 2); // +2 for left/right labels
        double availableHeight = totalHeight / (boardSize + 2); // +2 for top/bottom labels

        double squareSize = Math.min(availableWidth, availableHeight);

        boardPane.setWidth(boardSize * squareSize);
        boardPane.setHeight(boardSize * squareSize);

        for (Node node : grid.getChildren()) {
            if (node instanceof Label label) {
                label.setPrefSize(squareSize, squareSize);
            }
        }
    }



    /**
     * Creates a styled square label for use as a coordinate indicator around the chess board.
     *
     * <p>The label is centered, bold, and has a fixed size and border styling to visually
     * distinguish it from the board itself.
     *
     * @param text The text to display in the label (e.g., file letter or rank number).
     * @param size The width and height (in pixels) of the label.
     * @return A styled {@link Label} node ready to be placed in the UI.
     */
    private Label createLabel(String text, double size) {
        Label label = new Label(text);
        label.setPrefSize(size, size);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-weight: bold;");
        return label;
    }


    /**
     * Registers all UI event handlers and property change listeners for the chess application.
     *
     * <p>This includes:
     * <ul>
     *   <li>Listening to game state changes (e.g., board updates, player turns, game over, promotion).</li>
     *   <li>Handling menu actions such as starting a new game, saving/loading/importing/exporting games, and quitting.</li>
     *   <li>Switching between normal and learning modes with undo/redo support in learning mode.</li>
     *   <li>Managing pawn promotion UI and applying the player's selected piece.</li>
     *   <li>Controlling sound toggle and updating the icon accordingly.</li>
     * </ul>
     *
     * <p>All relevant UI components are wired to respond to user actions and updates from the {@link ChessGameManager}.
     */
    private void registerHandlers(){

        //listener to board state
        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_BOARD_STATE,
                evt-> {update();});

        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_CURRENT_PLAYER,
                evt-> {update();});



        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_GAME_OVER,
                evt-> {update();});

        chessGameManager.addPropertyChangeListener(ChessGameManager.PROP_PROMOTION,
                evt -> {
                    boolean isPromoting = (Boolean) evt.getNewValue();
                    promotionBox.setVisible(isPromoting);
                });


        //New menu option
        newItem.setOnAction(event -> {TextInputDialog dialog1 = new TextInputDialog();
            dialog1.setTitle("Player names");
            dialog1.setHeaderText("Enter the name of Player 1");
            dialog1.setContentText("");

            Button okButton1 = (Button) dialog1.getDialogPane().lookupButton(ButtonType.OK);
            okButton1.disableProperty().bind(
                    Bindings.createBooleanBinding(() ->
                                    dialog1.getEditor().getText().trim().isEmpty(),
                            dialog1.getEditor().textProperty())
            );

            dialog1.showAndWait().ifPresent(player1Name -> {
                // Second dialog: Player 2
                TextInputDialog dialog2 = new TextInputDialog();
                dialog2.setTitle("Player names");
                dialog2.setHeaderText("Enter the name of Player 2");
                dialog2.setContentText("");

                Button okButton2 = (Button) dialog2.getDialogPane().lookupButton(ButtonType.OK);
                okButton2.disableProperty().bind(
                        Bindings.createBooleanBinding(() ->
                                        dialog2.getEditor().getText().trim().isEmpty(),
                                dialog2.getEditor().textProperty())
                );

                dialog2.showAndWait().ifPresent(player2Name -> {
                    ModelLog.getInstance().addLog("Starting new game with names: " + player1Name + " and " + player2Name);
                    chessGameManager.start(player1Name, player2Name);

                });
            });
        });


        //Load Menu option
        loadItem.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("File to load");
            dialog.setHeaderText("Enter the name of the file");
            dialog.setContentText("");

            Button okButton2 = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton2.disableProperty().bind(
                    Bindings.createBooleanBinding(() ->
                                    dialog.getEditor().getText().trim().isEmpty(),
                            dialog.getEditor().textProperty())
            );

            dialog.showAndWait().ifPresent(file -> {
                ModelLog.getInstance().addLog("Starting new game with names: ");
              if(chessGameManager.loadGame(file))

                  ModelLog.getInstance().addLog("Game loaded");

            });
        });

        saveItem.setOnAction(event -> {
            chessGameManager.saveGame("Save");

        });
        importItem.setOnAction(event -> {
            TextInputDialog dialog1 = new TextInputDialog();
            dialog1.setTitle("Player names");
            dialog1.setHeaderText("Enter the name of Player 1");

            Button okButton1 = (Button) dialog1.getDialogPane().lookupButton(ButtonType.OK);

            okButton1.disableProperty().bind(
                    Bindings.createBooleanBinding(() ->
                                    dialog1.getEditor().getText().trim().isEmpty(),
                            dialog1.getEditor().textProperty())
            );

            dialog1.showAndWait().ifPresent(player1Name -> {
                // Second dialog: Player 2
                TextInputDialog dialog2 = new TextInputDialog();
                dialog2.setTitle("Player names");
                dialog2.setHeaderText("Enter the name of Player 2");
                dialog2.setContentText("");

                Button okButton3 = (Button) dialog2.getDialogPane().lookupButton(ButtonType.OK);
                okButton3.disableProperty().bind(
                        Bindings.createBooleanBinding(() ->
                                        dialog2.getEditor().getText().trim().isEmpty(),
                                dialog2.getEditor().textProperty())
                );

                dialog2.showAndWait().ifPresent(player2Name -> {
                    ModelLog.getInstance().addLog("Importing game with names: " + player1Name + " and " + player2Name);
                    chessGameManager.setPlayer1(player1Name);
                    chessGameManager.setPlayer2(player2Name);
                });
            });
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("File to import");
            dialog.setHeaderText("File name:");

            Button okButton2 = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton2.disableProperty().bind(
                    Bindings.createBooleanBinding(() -> dialog.getEditor().getText().trim().isEmpty(),
                            dialog.getEditor().textProperty())
            );

                    dialog.showAndWait().ifPresent(file -> {
                        ModelLog.getInstance().addLog("loading file: " + file);
                        if(chessGameManager.importGame(file)){
                                ModelLog.getInstance().addLog("Game loaded");
                            }
                    });

        });

        exportItem.setOnAction(event -> {
            chessGameManager.exportGame();
        });
        quitItem.setOnAction(event -> {
            System.exit(0);
        });

        learningItem.setOnAction(event -> {

            boardPane.toggleIsLearning();
            toggleMode();
        });

        normalItem.setOnAction(event -> {
            boardPane.toggleIsLearning();
            toggleMode();
        });

        undoItem.setOnAction(event -> {
            if(chessGameManager.hasUndo()){
                chessGameManager.undo();
            }
        });

        redoItem.setOnAction(event -> {
            if (chessGameManager.hasRedo()) {
                chessGameManager.redo();
            }
        });


        soundButton.setOnAction(event -> {
            boardPane.toggleSound();
            if(boardPane.isSoundOn()){
                ModelLog.getInstance().addLog("Sound is on!");
                soundButton.setText("\uD83D\uDD08");
            }else{
                ModelLog.getInstance().addLog("Sound off");
                soundButton.setText("\uD83D\uDD07");
            }
        });

        queenButton.setOnAction(e -> {
            chessGameManager.setPieceChoice('q');
            promotionBox.setVisible(false);
            chessGameManager.completePromotion();
        });

        rookButton.setOnAction(e -> {
            chessGameManager.setPieceChoice('r');
            promotionBox.setVisible(false);
            chessGameManager.completePromotion();
        });

        bishopButton.setOnAction(e -> {
            chessGameManager.setPieceChoice('b');
            promotionBox.setVisible(false);
            chessGameManager.completePromotion();
        });

        knightButton.setOnAction(e -> {
            chessGameManager.setPieceChoice('n');
            promotionBox.setVisible(false);
            chessGameManager.completePromotion();
        });

        final boolean isWhite[] = {true}; // toggle flag(has to be final)
        colorButton.setOnAction(e -> {
            isWhite[0] = !isWhite[0];
            if (isWhite[0]) {
                colorButton.setText("WHITE");
                chessGameManager.setColorChoice();
                ModelLog.getInstance().addLog("Current choice: " + chessGameManager.getColorChoice());
            } else {
                colorButton.setText("BLACK");
                chessGameManager.setColorChoice();
                ModelLog.getInstance().addLog("Current choice: " + chessGameManager.getColorChoice());
            }
        });

        editorBoxHandlers();
    }


    /**
     * Creates and initializes the menu bar for the chess application.
     *
     * <p>The menu bar contains two main menus:
     * <ul>
     *   <li><b>Game:</b> Includes options for starting a new game, loading, saving,
     *       importing, exporting games, and quitting the application.</li>
     *   <li><b>Mode:</b> Contains mode selection items such as Learning mode
     *       (currently only the learningItem is added).</li>
     * </ul>
     *
     * <p>This method also initializes menu items and adds them to their respective menus.
     *
     * @return the constructed {@link MenuBar} instance with configured menus and menu items
     */
    private MenuBar createMenu() {
        MenuBar mb = new MenuBar();

        gameMenu = new Menu("Game");
        newItem = new MenuItem("New");
        loadItem = new MenuItem("Load");
        saveItem = new MenuItem("Save");
        importItem = new MenuItem("Import");
        exportItem = new MenuItem("Export");
        quitItem = new MenuItem("Quit");

        editorItem = new MenuItem("Editor");


        SeparatorMenuItem separator = new SeparatorMenuItem();
        gameMenu.getItems().addAll(newItem, loadItem, saveItem, importItem, exportItem, separator, quitItem);

        modeMenu = new Menu("Mode");
        normalItem = new MenuItem("Normal");
        learningItem = new MenuItem("Learning");

        undoItem = new MenuItem("Undo");
        redoItem = new MenuItem("Redo");
        modeMenu.getItems().addAll(editorItem, learningItem);


        mb.getMenus().addAll(gameMenu, modeMenu);



        return mb;
    }


    /**
     * Toggles the mode menu items between Learning mode and Normal mode.
     *
     * <p>If the board is currently in Learning mode, this method removes the
     * learning mode menu item and adds the normal mode along with undo and redo items.
     * Otherwise, it switches back to show only the learning mode item.
     *
     * <p>This effectively updates the mode menu to reflect the current state of the game board.
     */
    private void toggleMode(){
        if(boardPane.isLearning()){
            modeMenu.getItems().removeAll(learningItem);
            modeMenu.getItems().addAll(normalItem, undoItem, redoItem);
        }else {
            modeMenu.getItems().removeAll(normalItem, undoItem, redoItem);
            modeMenu.getItems().addAll(learningItem);
        }
    }

    /**
     * Initializes and configures the promotion selection UI component.
     *
     * <p>This method creates buttons for each promotable piece (Queen, Rook,
     * Bishop, Knight), arranges them in a vertical layout, and styles the
     * promotion box. The box is initially hidden and only shown when a
     * pawn promotion is triggered in the game.
     */
    private void createPromotionBox() {
        queenButton = new Button("QUEEN");
        bishopButton = new Button("BISHOP");
        knightButton = new Button("KNIGHT");
        rookButton = new Button("ROOK");
        promotionBox = new VBox(10, queenButton, rookButton, bishopButton, knightButton);
        promotionBox.setAlignment(Pos.CENTER);
        promotionBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        promotionBox.setVisible(false);
    }


    /**
     * Initializes and configures the piece editor UI component used in board editing mode.
     *
     * <p>This method creates buttons for each type of chess piece (Bishop, King, Knight,
     * Pawn, Queen, Rook) as well as a toggle button to switch between white and black
     * piece colors. The color selection updates the piece color choice in the
     * {@code ChessGameManager}. The editor panel is arranged vertically and hidden by default.
     */
    private void createEditorBox() {
        addBishop = new Button("BISHOP");
        addKing = new Button("KING");
        addKnight = new Button("KNIGHT");
        addPawn = new Button("PAWN");
        addQueen = new Button("QUEEN");
        addRook = new Button("ROOK");

        colorButton = new Button("WHITE");
        
        editorBox = new VBox(10, colorButton, addBishop, addKnight, addKing, addQueen, addRook, addPawn);
        editorBox.setAlignment(Pos.CENTER);
        editorBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
        editorBox.setVisible(false);
    }


    /**
     * Sets up event handlers for the board editor controls.
     *
     * <p>This method configures the behavior of the editor menu item and piece selection buttons.
     * When the editor mode is toggled:
     * <ul>
     *   <li>If a game is running and not in edit mode, the user is prompted to choose whether to clear the board before entering edit mode.</li>
     *   <li>If the game is already in edit mode, it exits the editor.</li>
     * </ul>
     *
     * <p>Piece selection buttons allow the user to choose a piece type (rook, bishop, king, queen, pawn, knight)
     * to be placed on the board during editing.
     */
    private void editorBoxHandlers() {
        editorItem.setOnAction(e -> {
            if (!boardPane.isGameRunning()) {
                ModelLog.getInstance().addLog("Cannot enter editor: Game is not running.");
                return;
            }

            if (!chessGameManager.isEditing()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Editor mode");
                alert.setHeaderText("Clear board?");
                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                alert.getButtonTypes().setAll(yesButton, noButton);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == yesButton) {
                        chessGameManager.clearBoard();
                        ModelLog.getInstance().addLog("Clearing board and entering editor mode");
                    } else {
                        ModelLog.getInstance().addLog("Editing current board");
                    }
                    enterEditorMode();
                }
            } else {
                // Already in edit mode → exit
                exitEditorMode();
            }
        });

        addRook.setOnAction(e ->
                chessGameManager.setPieceChoice('r')
        );
        addBishop.setOnAction(e ->
                chessGameManager.setPieceChoice('b')
        );
        addKing.setOnAction(e ->
                chessGameManager.setPieceChoice('k')
        );
        addQueen.setOnAction(e ->
                chessGameManager.setPieceChoice('q')
        );
        addPawn.setOnAction(e -> chessGameManager.setPieceChoice('p'));
        addKnight.setOnAction(e -> chessGameManager.setPieceChoice('n'));
    }


    /**
     * Activates the board editor mode.
     *
     * <p>This method sets the editing state in the {@code ChessGameManager} to {@code true},
     * makes the editor controls visible, and updates the current player label
     * to indicate that the application is in editing mode.
     */
    private void enterEditorMode() {
        chessGameManager.setIsEditing(true);
        editorBox.setVisible(true);
        currentPlayerLabel.setText("Editing Mode");
    }


    /**
     * Exits the board editor mode.
     *
     * <p>This method disables editing mode in the {@code ChessGameManager},
     * hides the editor controls, and clears the current player label to reflect
     * the return to normal game state.
     */
    private void exitEditorMode() {
        chessGameManager.setIsEditing(false);
        editorBox.setVisible(false);
        currentPlayerLabel.setText(chessGameManager.getCurrentPlayer());
    }



    /**
     * Toggles the board editing mode on or off.
     *
     * <p>This method inverts the current editing state managed by {@code ChessGameManager},
     * updates the visibility of the editor UI, and sets the label text accordingly.
     * When editing is enabled, "Editing Mode" is shown; otherwise, the label is cleared.
     */
    private void toggleIsEditing() {
        boolean newEditingState = !chessGameManager.isEditing();
        chessGameManager.setIsEditing(newEditingState);
        editorBox.setVisible(newEditingState);

        if (newEditingState) {
            currentPlayerLabel.setText("Editing Mode");
        } else {
            currentPlayerLabel.setText("");
        }
    }


    /**
     * Updates the UI elements based on the current state of the chess game.
     *
     * <p>If the game is in a checkmate state, it stops the game and displays the ending state.
     * Otherwise, if the game is running, it updates the current player label to reflect
     * whose turn it is.
     *
     * <p>Does nothing if the currentPlayerLabel is not initialized.
     */

    private void update(){
        if (currentPlayerLabel != null) {
            if(chessGameManager.isCheckMate()){
                boardPane.toggleIsGameRunning();
                currentPlayerLabel.setText(chessGameManager.getEndingState());
                return;
            }

            if (boardPane.isGameRunning()) {
                currentPlayerLabel.setText("Current: " + chessGameManager.getCurrentPlayer());
            }
        }
    }

}
