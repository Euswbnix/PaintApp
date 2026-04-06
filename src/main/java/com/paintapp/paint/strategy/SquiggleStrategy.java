package com.paintapp.paint.strategy;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;
import com.paintapp.paint.shapes.Point;
import com.paintapp.paint.shapes.ShapeFactory;
import com.paintapp.paint.shapes.Squiggle;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.List;

/**
 * SquiggleStrategy handles teh drawing process for the squiggles. Using MouseEvent to
 * receive the mouse interaction to draw the drawable shapes based on the
 * mouse interactions.
 */
public class SquiggleStrategy implements DrawingStrategy {
    /**
     * Squiggle object that represents the current squiggle shape.
     */
    private Squiggle currentSquiggle = null;
    /**
     * Boolean that is true if the shape is being dragged by the mouse, else false.
     */
    private boolean isMoving = false;
    /**
     * Arraylist for all the squiggles that are being moved.
     */
    private final List<Drawable> moving = new java.util.ArrayList<>();
    /**
     * double object lastX, lastY that represents the position of last click
     * from the mouse.
     */
    private double lastX, lastY;
    /**
     * double object totalDX, totalDY represents the change of position from the
     * initial click to when the mouse is released.
     */
    private double totalDX, totalDY;
    /**
     * boolean object that represents if the selection of the shape is cleared
     */
    private boolean clearedSelection = false;

    /**
     * Handles the drawing process for drawing squiggles. When the mouse is first clicked the initial click
     * position is saved and when dragging your mouse the feedback is updated to animate the
     * shape being dragged. Then, when the mouse is released the squiggle is finalized, the squiggle is
     * added to the program screen.
     * @param e
     * @param model
     * @param color
     * @param lineWidth
     * @param fillStyle
     */
    @Override
    public void handle(MouseEvent e, PaintModel model, Color color, double lineWidth, FillStyle fillStyle) {
        switch (e.getEventType().getName()) {
            case "MOUSE_PRESSED" -> {
                Drawable hit = null;
                List<Drawable> ds = model.getDrawables();
                for (Drawable selected : model.getSelectedShapes()) {
                    double[] bounds = selected.getBounds();
                    double x = bounds[0], y = bounds[1], w = bounds[2], h = bounds[3];
                    if (e.getX() >= x && e.getX() <= x + w &&
                            e.getY() >= y && e.getY() <= y + h) {
                        hit = selected;
                        break;
                    }
                }
                if (hit == null) {
                    for (int i = ds.size() - 1; i >= 0; i--) {
                        if (ds.get(i).contains(e.getX(), e.getY())) {
                            hit = ds.get(i);
                            break;
                        }
                    }
                }
                if (hit != null && model.getSelectedShapes().contains(hit)) {
                    isMoving = true;
                    moving.clear();
                    moving.addAll(model.getSelectedShapes());
                    lastX = e.getX();
                    lastY = e.getY();
                    totalDX = totalDY = 0.0;
                    clearedSelection = false;
                } else {
                    if (!model.getSelectedShapes().isEmpty()) {
                        model.clearSelection();
                        clearedSelection = true;
                        currentSquiggle = null;
                    } else {
                        isMoving = false;
                        clearedSelection = false;
                        currentSquiggle = (Squiggle) ShapeFactory.createShape("squiggle", color, lineWidth, fillStyle,
                                new Point[]{new Point(e.getX(), e.getY(), color)});
                        currentSquiggle.addPoint(new Point(e.getX(), e.getY(), color));
                        model.setCurrentDrawable(currentSquiggle);
                    }
                }
            }
            case "MOUSE_DRAGGED" -> {
                if (isMoving) {
                    Canvas c = (Canvas) e.getSource();
                    double W = c.getWidth(), H = c.getHeight();
                    double reqDX = e.getX() - lastX;
                    double reqDY = e.getY() - lastY;
                    double minDx = Double.NEGATIVE_INFINITY, maxDx = Double.POSITIVE_INFINITY;
                    double minDy = Double.NEGATIVE_INFINITY, maxDy = Double.POSITIVE_INFINITY;
                    for (Drawable d : moving) {
                        double[] b = d.getBounds();
                        double x = b[0], y = b[1], w = b[2], h = b[3];
                        minDx = Math.max(minDx, -x);
                        maxDx = Math.min(maxDx, W - (x + w));
                        minDy = Math.max(minDy, -y);
                        maxDy = Math.min(maxDy, H - (y + h));
                    }
                    double dx = Math.max(minDx, Math.min(maxDx, reqDX));
                    double dy = Math.max(minDy, Math.min(maxDy, reqDY));
                    for (Drawable d : moving) {
                        d.offset(dx, dy);
                    }
                    model.moveUpdate();
                    totalDX += dx;
                    totalDY += dy;
                    lastX = e.getX();
                    lastY = e.getY();
                } else if (!clearedSelection && currentSquiggle != null) {
                    currentSquiggle.addPoint(new Point(e.getX(), e.getY(), color));
                    model.setCurrentDrawable(currentSquiggle);
                }
            }
            case "MOUSE_RELEASED" -> {
                if (isMoving) {
                    if (Math.abs(totalDX) > 0.1 || Math.abs(totalDY) > 0.1) {
                        for (Drawable d : moving) d.offset(-totalDX, -totalDY);
                        model.moveUpdate();
                        model.moveSelectedBy(totalDX, totalDY);
                    }
                    moving.clear();
                    isMoving = false;
                } else if (!clearedSelection && currentSquiggle != null) {
                    model.addDrawableWithCommandNoSelect(currentSquiggle);
                    model.clearCurrentDrawable();
                    currentSquiggle = null;
                    System.out.println("Added squiggle");
                }
                clearedSelection = false;
            }
        }
    }
}