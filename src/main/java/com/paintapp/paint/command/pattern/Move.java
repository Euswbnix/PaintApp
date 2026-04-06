package com.paintapp.paint.command.pattern;

import com.paintapp.paint.shapes.Drawable;

import java.util.List;

/**
 * Representation of the move operation to move shapes from one position to another
 * based on the mouse clicking the shape, then dragging and releasing shapes to a new position.
 * We can execute the operation or undo our previous execution.
 */
public class Move implements Command {
    /**
     * Arraylist that contains the drawable shapes that are selected.
     */
    private final List<Drawable> targets;
    /**
     * double dx, dy represents the change in x-axis and y-axis.
     */
    private final double dx, dy;

    /**
     * Contracts a Move object with the selected shapes added to the
     * targets ArrayList and dx, dy are initialized.
     * @param targets
     */
    public Move(List<Drawable> targets, double dx, double dy) {
        this.targets = targets;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Runs the move action on the selected shapes.
     */
    @Override
    public void execute() {
        for (Drawable d : targets) {
            d.offset(dx, dy);
        }
    }

    /**
     * Undoes the move action such that the selected shapes is moved back
     * to their original position.
     */
    @Override
    public void undo() {
        for (Drawable d : targets) {
            d.offset(-dx, -dy);
        }
    }
}
