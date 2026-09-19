package pt.isec.pa.chess.model.caretaker;


import pt.isec.pa.chess.model.memento.IMemento;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Caretaker class in the Memento design pattern.
 * <p>
 * Manages undo and redo operations by storing and restoring {@link IMemento} objects.
 * It maintains two stacks: one for undo history and one for redo history.
 *
 * @see IMemento
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 */

public class Caretaker {
    /**
     * array of IMemento to save
     * */
    private final Deque<IMemento> history = new ArrayDeque<>();
    /**
     * array of Imementos to undo
     * */
    private final Deque<IMemento> redoMoves = new ArrayDeque<>();
    /**
     * Saves the given memento state to the history stack and clears the redo stack.
     *
     * @param memento the state to be saved
     */
    public void saveState(IMemento memento) {
        history.push(memento);
        redoMoves.clear();
    }

    /**
     * Undoes the last operation by restoring the previous state.
     * The current state is pushed to the redo stack for potential reapplication.
     *
     * @param currentState the current state before undo
     * @return the previous state, or <code>null</code> if no history is available
     */
    public IMemento undo(IMemento currentState) {
        if (history.isEmpty())
            return null;

        redoMoves.push(currentState);
        return history.pop();
    }

    /**
     * Redoes the last undone operation by restoring a state from the redo stack.
     * The current state is pushed back to the undo history.
     *
     * @param currentState the current state before redo
     * @return the redone state, or <code>null</code> if no redo is available
     */
    public IMemento redo(IMemento currentState) {
        if (redoMoves.isEmpty())
            return null;

        history.push(currentState);
        return redoMoves.pop();
    }
    /**
     * Checks if there is a state available to undo.
     *
     * @return <code>true</code> if undo is possible; <code>false</code> otherwise
     */
    public boolean canUndo() {
        return !history.isEmpty();
    }
    /**
     * Checks if there is a state available to redo.
     *
     * @return <code>true</code> if redo is possible; <code>false</code> otherwise
     */
    public boolean canRedo() {
        return !redoMoves.isEmpty();
    }
    /**
     * Clears all stored undo and redo history.
     */
    public void clearHistory() {
        history.clear();
        redoMoves.clear();
    }
}
