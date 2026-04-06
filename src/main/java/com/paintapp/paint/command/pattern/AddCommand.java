package com.paintapp.paint.command.pattern;

import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;

/**
 * Implements the command interface to execute or undo specific actions
 * such as deleting, moving, pasting, etc.
 */
public class AddCommand implements Command {
    /**
     * A PaintModel object to interact and execute or undo actions on screen.
     */
    private PaintModel model;
    /**
     * A drawable shape that we execute or undo actions on.
     */
    private Drawable drawable;

    /**
     * Constructs a new AddCommand object for the selected drawable shape on the given.
     * paint model.
     * @param model
     * @param drawable
     */
    public AddCommand(PaintModel model, Drawable drawable) {
        this.model = model;
        this.drawable = drawable;
    }

    /**
     * Runs the action of the outlined command operation on the selected.
     * drawable shape.
     */
    @Override
    public void execute() {
        model.addDrawableQuiet(drawable);
    }

    /**
     * Undoes the most recent action of the outlined command operation on
     * the selected drawable shape.
     */
    @Override
    public void undo() {
        model.removeDrawableQuiet(drawable);
    }
}
