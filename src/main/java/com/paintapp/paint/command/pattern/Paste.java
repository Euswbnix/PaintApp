package com.paintapp.paint.command.pattern;

import com.paintapp.paint.app.Clipboard;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;

import java.util.List;

/**
 * Representation of the Paste operation.
 * Once a shape or many shapes are selected and copied the paste action
 * can be executed to paste the selected shapes.
 * We can execute the operation or undo our previous execution.
 */
public class Paste implements Command {
    /**
     * A PaintModel object to interact and execute or undo actions on screen.
     */
    private final PaintModel model;
    /**
     * Arraylist that contains the drawable shapes that will be pasted
     * when the paste action is executed.
     */
    private List<Drawable> pastedShapes;

    /**
     * Contracts a Paste object based on the given paint model.
     * @param model
     */
    public Paste(PaintModel model) {
        this.model = model;
    }

    /**
     * Gets the shapes copied to the clipboard and draws them onto the paint model.
     */
    @Override
    public void execute() {
        pastedShapes = Clipboard.getInstance().paste();
        for (Drawable s : pastedShapes) {
            model.addDrawableQuiet(s);
        }
    }

    /**
     * Undoes the Paste action such that the most recent pasted shapes are removed
     */
    @Override
    public void undo() {
        if (pastedShapes != null) {
            for (Drawable s : pastedShapes) {
                model.removeDrawableQuiet(s);
            }
        }
    }

    /**
     * RETURNS the shapes pasted
     * @return pastedShapes ArrayList
     */
    public List<Drawable> getPastedshapes() {
        return pastedShapes;
    }
}
