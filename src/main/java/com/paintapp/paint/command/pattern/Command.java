package com.paintapp.paint.command.pattern;

/**
 * Command interface is the representation of operations
 * that can be executed and undone which is used to implement the
 * design command pattern.
 * Thus, decoupling the object that invokes the commands and the object
 * that executes the command.
 *
 */
public interface Command {
    /**
     * Runs the action of the outlined command operation.
     */
    void execute();

    /**
     * Undoes the action of the outlined command operation that was most recently
     * executed.
     */
    void undo();
}
