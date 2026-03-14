package catandomainmodel;

import java.util.Stack;

/**
 * Manages the history of game commands for undo/redo functionality.
 */
public class CommandManager {
    private Stack<GameCommand> undoStack = new Stack<>();
    private Stack<GameCommand> redoStack = new Stack<>();

    /**
     * Executes a command and adds it to the undo stack.
     * Clears the redo stack.
     */
    public void executeCommand(GameCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undoes the last command if possible.
     */
    public boolean undo() {
        if (!undoStack.isEmpty()) {
            GameCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            return true;
        }
        return false;
    }

    /**
     * Redoes the last undone command if possible.
     */
    public boolean redo() {
        if (!redoStack.isEmpty()) {
            GameCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            return true;
        }
        return false;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
