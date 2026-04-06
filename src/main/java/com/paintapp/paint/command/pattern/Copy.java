package com.paintapp.paint.command.pattern;

import com.paintapp.paint.app.Clipboard;
import com.paintapp.paint.shapes.Drawable;

import java.util.List;

/**
 * Representation of the copy operation to copy shapes.
 * The copy operation copies the selected shapes such that they can be pasted
 * using the paste action. We can execute the operation or undo our previous execution.
 */
public class Copy implements Command {
    /**
     * Arraylist that contains the selected drawable shapes.
     */
    private final List<Drawable> selectedShapes;

    /**
     * Contracts a Copy object with the selected shapes added to the selectedShapes ArrayList.
     * @param selectedShapes
     */
    public Copy(List<Drawable> selectedShapes) {
        this.selectedShapes = selectedShapes;
    }

    /**
     * Runs the copy action on the selected shapes.
     */
    @Override
    public void execute() {
        Clipboard.getInstance().copy(selectedShapes);
    }

    /**
     * Unimplemented, because when shapes are copied it cannot be undone.
     */
    @Override
    public void undo() {
    }

}
