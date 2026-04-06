package com.paintapp.paint.command.pattern;

import com.paintapp.paint.app.Clipboard;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the cut operation to cut shapes.
 * The cut operation deletes the selected shapes and copies them such that
 * they can be pasted using the paste action.
 * We can execute the operation or undo our previous execution.
 */
public class Cut implements Command {
    /**
     * A PaintModel object to interact and execute or undo actions on screen.
     */
    private final PaintModel model;
    /**
     * Arraylist that contains the drawable shapes that will be cut
     * when the cut action is executed.
     */
    private List<Drawable> cutShapes;

    /**
     * Contracts a Cut object with the selected shapes added to the cutShapes ArrayList.
     * @param selectedShapes
     */
    public Cut(PaintModel model, List<Drawable> selectedShapes) {
        this.model = model;
        this.cutShapes = new ArrayList<>(selectedShapes);
    }

    /**
     * Runs the cut action on the selected shapes.
     */
    @Override
    public void execute() {
        Clipboard.getInstance().copy(cutShapes);
        for (Drawable s : cutShapes) {
            model.removeDrawableQuiet(s);
        }
    }

    /**
     * Undoes the deletion process from the cut action such that the
     * most recent deleted shapes are readded.
     */
    @Override
    public void undo() {
        for (Drawable s : cutShapes) {
            model.addDrawableQuiet(s);
        }
    }
}
