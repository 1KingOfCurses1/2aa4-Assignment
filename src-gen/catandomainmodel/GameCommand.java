package catandomainmodel;

/**
 * Interface for game commands that can be executed and undone.
 */
public interface GameCommand {
    /**
     * Executes the command, modifying the game state.
     */
    void execute();

    /**
     * Undoes the command, reverting the game state.
     */
    void undo();

    /**
     * Returns a human-readable description of the command.
     */
    String describe();
}
