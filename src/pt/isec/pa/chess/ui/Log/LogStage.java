package pt.isec.pa.chess.ui.Log;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ModelLog;


/**
 * LogStage is a JavaFX Stage that displays application logs in a ListView.
 * It provides a button to clear the logs and updates the view in real-time
 * by listening to changes in the ModelLog singleton.
 */
public class LogStage extends Stage {
    private static ListView<String> logList;
    private static Button clearButton;

    /**
     * Constructs a LogStage window with a title "Logs", initializes the UI components,
     * sets up event handlers, and sets the scene with a VBox layout containing
     * the log list and a clear button.
     */
    public LogStage() {
        setTitle("Logs");


        createViews();
        registerHandlers();

        VBox root = new VBox(10, logList, clearButton);
        Scene scene = new Scene(root, 400, 300);

        setScene(scene);
    }

    /**
     * Initializes the UI components: a ListView to display logs and a button to clear logs.
     * The ListView is populated with existing logs from the ModelLog singleton.
     */

    private static void createViews() {
        logList = new ListView<>();
        logList.getItems().addAll(ModelLog.getInstance().getLogs());

        clearButton = new Button("Clear Logs");
    }


    /**
     * Registers event handlers and listeners for UI interactions and log updates.
     * - Clears the log list when the clear button is pressed.
     * - Clears the ListView when logs are cleared in the ModelLog.
     * - Adds new log entries to the ListView when a log is added in the ModelLog.
     */

    private static void registerHandlers() {
        clearButton.setOnAction(e -> ModelLog.getInstance().clearLogs());

        ModelLog.getInstance().addPropertyChangeListener(
                ModelLog.PROP_LOGS_CLEARED,
                evt -> {logList.getItems().clear();});
        ModelLog.getInstance().addPropertyChangeListener(
                ModelLog.PROP_LOG_ADDED,
                evt -> {logList.getItems().add((String) evt.getNewValue());}
        );
    }
}

