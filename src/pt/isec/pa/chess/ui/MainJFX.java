package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.ui.Log.LogStage;


/**
 * Main JavaFX application class that initializes the chess game manager,
 * sets up the main game window and an additional game window,
 * and opens a separate log window for displaying game logs.
 */
public class MainJFX extends Application{
    private ChessGameManager manager;
    @Override
    public void init() throws Exception {
        super.init();
        manager = new ChessGameManager();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // main game
        RootPane rootPane1 = new RootPane(600, manager);
        Scene scene1 = new Scene(rootPane1, 1366, 720);

        Stage stage2 = new Stage();
        RootPane rootPane2 = new RootPane(600, manager);
        Scene scene2 = new Scene(rootPane2, 1366, 720);


        stage.setTitle("PAChess");
        stage.setScene(scene1);
        stage.show();


        LogStage log = new LogStage();
        log.show();


    }
}