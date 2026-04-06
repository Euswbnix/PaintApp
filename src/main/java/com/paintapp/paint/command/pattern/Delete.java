package com.paintapp.paint.command.pattern;

import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the delete operation to remove shapes.
 * The delete operation removes the selected shapes from the paint panel.
 * We can execute the operation or undo our previous execution.
 */
public class Delete implements Command {
    /**
     * A PaintModel object to interact and execute or undo actions on screen.
     */
    private final PaintModel model;
    /**
     * Arraylist that contains the drawable shapes that will be deleted
     * when the delete action is executed.
     */
    private final List<Drawable> deletedShapes;

    /**
     * Contracts a Delete object with the selected shapes added to the
     * deletedShapes ArrayList.
     * @param selectedShapes
     */
    public Delete(PaintModel model, List<Drawable> selectedShapes) {
        this.model = model;
        this.deletedShapes = new ArrayList<>(selectedShapes);
    }

    /**
     * Runs the delete action on the selected shapes.
     */
    @Override
    public void execute() {
        for (Drawable s : deletedShapes) {
            model.removeDrawableQuiet(s);
        }
    }

    /**
     * Undoes the delete action such that the most recent deleted shapes are readded.
     */
    @Override
    public void undo() {
        for (Drawable s : deletedShapes) {
            model.addDrawableQuiet(s);
        }
    }
}
