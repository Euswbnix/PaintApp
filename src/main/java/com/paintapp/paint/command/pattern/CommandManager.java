package com.paintapp.paint.command.pattern;

import java.util.Stack;

/**
 * The CammandManger is responsible for coordinating the execution and reversal of command objects.
 * It maintains two stacks. One for commands that have been executed and can be undone,
 * and one for the commands that have been undone and can be redone.
 *
 */
public class CommandManager {
    /**
     * Stack object that contains the commands that have been executed and can be undone.
     * Stack is final, because the reference to the stack is not changed, but the contents
     * in the stack are regularly changed.
     */
    private final Stack<Command> undoStack = new Stack<>();
    /**
     * Stack object that contains the commands that have been undone and can be redone.
     * Stack is final, because the reference to the stack is not changed, but the contents
     * in the stack are regularly changed.
     */
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Runs the action of the outlined command operation and adds the command to the undo stack.
     * While clearing the redo stack.
     */
    public void execute(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undoes and removes the most recently added command to the undo stack.
     * While adding the command to the redo stack.
     */
    public void undo() {
        if (undoStack.isEmpty()) return;
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    /**
     * Redoes and removes the most recently added command to the redo stack.
     * While adding the command to the undo stack.
     */
    public void redo() {
        if (redoStack.isEmpty()) return;
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);

    }

    /**
     * RETURNS true if the undo stack is not empty
     * @return true if the undoStack is not empty
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * RETURNS true if the redo stack is not empty
     * @return true if the redoStack is not empty
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Wipes both the undo and redo stacks. Used when the user starts a
     * brand-new document so that previous edits cannot be undone into the
     * fresh canvas.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
