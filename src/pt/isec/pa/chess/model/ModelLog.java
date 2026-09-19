package pt.isec.pa.chess.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that maintains a log of events for the model layer.
 * <p>
 * Supports adding and clearing log entries, and notifies observers
 * of changes using {@link PropertyChangeSupport}.
 * <p>
 * Used for debugging messages, event history, or display in the UI.
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 * */

public class ModelLog {
    /** The single instance of the log manager (singleton pattern). */
    private static ModelLog instance = new ModelLog();

    /** Property name used to signal that a log entry was added. */
    public static final String PROP_LOG_ADDED = "logAdded";
    /** Property name used to signal that the log was cleared. */
    public static final String PROP_LOGS_CLEARED = "logsCleared";
    /** List that stores the log entries. */
    private final List<String> logs;
    /** Support for property change listeners to notify observers of changes. */
    private final PropertyChangeSupport support;
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the log list and property change support.
     */

    private ModelLog(){
        logs = new ArrayList<>();
        support = new PropertyChangeSupport(this);
    }

    /**
     * Adds a new entry to the log and notifies listeners.
     *
     * @param logEntry the log message to be added
     */

    public void addLog(String logEntry){
        logs.add(logEntry);
        support.firePropertyChange(PROP_LOG_ADDED, null, logEntry);
    }

    /**
     * Returns the current list of log entries.
     *
     * @return the list of log messages
     */
    public List<String> getLogs(){
        return logs;
    }
    /**
     * Clears all log entries and notifies listeners.
     */

    public void clearLogs(){
        logs.clear();
        support.firePropertyChange(PROP_LOGS_CLEARED, null, null);
    }

    /**
     * Adds a property change listener for a specific property.
     *
     * @param property the name of the property to listen to
     * @param listener the listener to be notified when the property changes
     */
    public void addPropertyChangeListener(String property,PropertyChangeListener listener) {
        support.addPropertyChangeListener(property,listener);
    }
    /**
     * Returns the singleton instance of the log manager.
     *
     * @return the shared {@code ModelLog} instance
     */

    public static ModelLog getInstance(){
        return instance;
    }
}
