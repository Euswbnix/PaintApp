package com.paintapp.paint.app;

import com.paintapp.paint.command.pattern.*;
import com.paintapp.paint.shapes.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Representations the model that stores all the data for the application, representing model in
 * the MVC architecture.
 * We are able to add shapes, trigger commands, and preform general mechanisms in our model which
 * changes the data in our application.
 */
public class PaintModel extends Observable {
    /**
     * ArrayList object the holds all the drawable shapes the user has drawn.
     */
    private final ArrayList<Drawable> drawables = new ArrayList<>();
    /**
     * Drawable object that represents the current drawable shape.
     */
    private Drawable currentDrawable = null;
    /**
     * CommandManager that allows us to execute or undo command actions.
     */
    private final CommandManager commandManager = new CommandManager();
    /**
     * ArrayList object the holds all the selected drawable shapes the user has selected.
     */
    private final List<Drawable> selectedShapes = new ArrayList<>();

    /**
     * Adds the drawable shape to our drawables ArrayLst without notifying the observers.
     * @param d
     */
    public void addDrawableQuiet(Drawable d) {
        drawables.add(d);
    }

    /**
     * Removes the drawable shape from our drawables ArrayLst without notifying the observers.
     * @param d
     */
    public void removeDrawableQuiet(Drawable d) {
        drawables.remove(d);
    }

    /**
     * RETURNS the ArrayList containing the drawn drawable shapes.
     * @return drawables ArrayList
     */
    public ArrayList<Drawable> getDrawables() {
        return drawables;
    }

    /**
     * Changes the currentDrawable shape to d while notifying the observers to invoke
     * the update method.
     * @param d
     */
    public void setCurrentDrawable(Drawable d) {
        this.currentDrawable = d;
        setChanged();
        notifyObservers();
    }

    /**
     * Clears the currentDrawable shape while notifying the observers to invoke
     * the update method.
     */
    public void clearCurrentDrawable() {
        this.currentDrawable = null;
        setChanged();
        notifyObservers();
    }

    /**
     * Draws the current drawable shape. While drawing the outline for the
     * selected objects.
     * @param g2d input
     */
    public void draw(GraphicsContext g2d) {
        for (Drawable d : drawables) {
            d.draw(g2d);
        }
        if (currentDrawable != null) {
            currentDrawable.draw(g2d);
        }
        selectedShapes.removeIf(shape -> !drawables.contains(shape));
        if (!selectedShapes.isEmpty()) {
            g2d.setStroke(Color.DODGERBLUE);
            g2d.setLineWidth(2);
            g2d.setLineDashes(8, 8);
            for (Drawable selected : selectedShapes) {
                double[] bounds = selected.getBounds();
                double x = bounds[0];
                double y = bounds[1];
                double width = bounds[2];
                double height = bounds[3];
                double padding = 5;
                g2d.strokeRect(x - padding, y - padding, width + padding * 2, height + padding * 2);
                double handleSize = 6;
                g2d.setFill(Color.DODGERBLUE);
                g2d.setLineDashes(0);
                g2d.fillRect(x - padding - handleSize / 2, y - padding - handleSize / 2, handleSize, handleSize);
                g2d.fillRect(x + width + padding - handleSize / 2, y - padding - handleSize / 2, handleSize, handleSize);
                g2d.fillRect(x - padding - handleSize / 2, y + height + padding - handleSize / 2, handleSize, handleSize);
                g2d.fillRect(x + width + padding - handleSize / 2, y + height + padding - handleSize / 2, handleSize, handleSize);
                g2d.setLineDashes(8, 8);
            }
            g2d.setLineDashes(0);

        }

        g2d.setLineDashes(0);
    }

    /**
     * Executes the command on the drawable d then clears the selected shapes and add
     * drawable d the selected shapes while notifying the observers to invoke.
     * the update method.
     * @param d
     */
    public void addDrawableWithCommand(Drawable d) {
        Command c = new AddCommand(this, d);
        commandManager.execute(c);
        selectedShapes.clear();
        selectedShapes.add(d);
        setChanged();
        notifyObservers();
    }

    /**
     * Undoes the most recent command acton.
     */
    public void undo() {
        commandManager.undo();
        clearSelection();
    }

    /**
     * Redoes the most recent command acton that was undone.
     */
    public void redo() {
        commandManager.redo();
        clearSelection();
    }

    /**
     * Get the selected shapes at position (x, y) and notify the observers
     * @param x
     * @param y
     */
    public void selectShapeAt(double x, double y) {
        selectedShapes.clear();
        for (int i = drawables.size() - 1; i >= 0; i--) {
            Drawable s = drawables.get(i);
            if (s.contains(x, y)) {
                selectedShapes.add(s);
                break;
            }
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Clear the selected shapes and update the observers.
     */
    public void clearSelection() {
        selectedShapes.clear();
        setChanged();
        notifyObservers();
    }

    /**
     * RETURN the shapes in selectedShapes as an ArrayList
     * @return ArrayList containing the shapes in selectedShapes
     */
    public List<Drawable> getSelectedShapes() {
        return new ArrayList<>(selectedShapes);
    }

    /**
     * Creates a new copy command and executes it on the selected shapes
     */
    public void copy() {
        if (!selectedShapes.isEmpty()) {
            Command c = new Copy(selectedShapes);
            c.execute();
        }
    }

    /**
     * Creates a new cut command and executes it on the selected shapes
     */
    public void cut() {
        if (!selectedShapes.isEmpty()) {
            Command c = new Cut(this, selectedShapes);
            commandManager.execute(c);
            selectedShapes.clear();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Creates a new delete command and executes it on the selected shapes
     */
    public void delete() {
        if (!selectedShapes.isEmpty()) {
            Command c = new Delete(this, selectedShapes);
            commandManager.execute(c);
            selectedShapes.clear();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Creates a new paste command and executes it on the selected shapes
     */
    public void paste() {
        if (!Clipboard.getInstance().isEmpty()) {
            Command c = new Paste(this);
            commandManager.execute(c);
            List<Drawable> pasted = ((Paste) c).getPastedshapes();
            if (pasted != null) {
                selectedShapes.clear();
                selectedShapes.addAll(pasted);
                setChanged();
                notifyObservers();
            }
        }
    }

    /**
     * Creates a new delete command and executes it on the selected shapes
     */
    public void moveSelectedBy(double dx, double dy) {
        if (this.getSelectedShapes() == null || this.getSelectedShapes().isEmpty()) return;
        Command c = new Move(new java.util.ArrayList<>(this.getSelectedShapes()), dx, dy);
        this.commandManager.execute(c);
        setChanged();
        notifyObservers();
    }

    /**
     * updates the observers to initiate the move action.
     */
    public void moveUpdate() {
        setChanged();
        notifyObservers();
    }

    /**
     * Executes the command on the drawable d while notifying the observers to invoke.
     * the update method.
     * @param d
     */
    public void addDrawableWithCommandNoSelect(Drawable d) {
        Command c = new AddCommand(this, d);
        commandManager.execute(c);
        setChanged();
        notifyObservers();
    }

    /**
     * Clears the selected shapes array and adds the new series of shapes that have
     * been selected, then update the observers.
     * @param shapes
     */
    public void selectMultipleShapes(List<Drawable> shapes) {
        selectedShapes.clear();
        selectedShapes.addAll(shapes);
        setChanged();
        notifyObservers();
    }

    /**
     * Updates the color for all the selected shapes.
     * @param color
     */
    public void updateSelectedShapesColor(Color color) {
        for (Drawable d : selectedShapes) {
            d.setColor(color);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Updates the line width for all the selected shapes.
     * @param lineWidth
     */
    public void updateSelectedShapesLineWidth(double lineWidth) {
        for (Drawable d : selectedShapes) {
            d.setLineWidth(lineWidth);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Updates the fill style for all the selected shapes.
     * @param fillStyle
     */
    public void updateSelectedShapesFillStyle(FillStyle fillStyle) {
        for (Drawable d : selectedShapes) {
            d.setFillStyle(fillStyle);
        }
        setChanged();
        notifyObservers();
    }
}
