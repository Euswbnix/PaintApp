package com.paintapp.paint.strategy;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;
import com.paintapp.paint.shapes.Point;
import com.paintapp.paint.shapes.ShapeFactory;
import com.paintapp.paint.shapes.Square;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * SquareStrategy handles teh drawing process for the squares. Using MouseEvent to receive the
 * mouse interaction to draw the drawable shapes based on the mouse interactions.
 */
public class SquareStrategy implements DrawingStrategy {
    /**
     * Square object that represents the current square shape.
     */
    private Square currentSquare;
    /**
     * Boolean that is true if the shape is moving, else false.
     */
    private boolean isMoving = false;
    /**
     * Boolean that is true if the shape is being dragged by the mouse, else false.
     */
    private boolean isDragging = false;
    /**
     * Arraylist for all the squares that are being moved.
     */
    private final List<Drawable> moving = new ArrayList<>();
    /**
     * double object lastX, lastY that represents the position of last click
     * from the mouse.
     */
    private double lastX, lastY;
    /**
     * double object startX, startY that represents the position of initial click
     * from the mouse.
     */
    private double startX, startY;
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
     * double object that represents the threshold for which the shape
     * is considered being dragged.
     */
    private static final double DRAG_THRESHOLD = 3.0;

    /**
     * Handles the drawing process for drawing square. When the mouse is first clicked the initial click
     * position is saved and when dragging your mouse the feedback is updated to animate the
     * shape being dragged. Then, when the mouse is released the square is finalized, the square is
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
                startX = e.getX();
                startY = e.getY();
                isDragging = false;
                
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
                if (hit != null) {
                    if (model.getSelectedShapes().contains(hit)) {
                        isMoving = true;
                        moving.clear();
                        moving.addAll(model.getSelectedShapes());
                        lastX = e.getX();
                        lastY = e.getY();
                        totalDX = totalDY = 0.0;
                        clearedSelection = false;
                    } else {
                        model.selectShapeAt(e.getX(), e.getY());
                        clearedSelection = false;
                    }
                    currentSquare = null;
                } else {
                    if (!model.getSelectedShapes().isEmpty()) {
                        model.clearSelection();
                        clearedSelection = true;
                        currentSquare = null;
                    } else {
                        isMoving = false;
                        clearedSelection = false;
                        currentSquare = null;
                    }
                }
            }
            case "MOUSE_DRAGGED" -> {
                double dragDistance = Math.hypot(e.getX() - startX, e.getY() - startY);
                if (dragDistance > DRAG_THRESHOLD) {
                    isDragging = true;
                }
                
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
                } else if (!clearedSelection && isDragging) {
                    currentSquare = (Square) ShapeFactory.createShape(
                            "square", color, lineWidth, fillStyle,
                            new Point[]{new Point(startX, startY, color), new Point(e.getX(), e.getY(), color)});
                    currentSquare.setDragging(true);
                    model.setCurrentDrawable(currentSquare);
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
                } else if (!clearedSelection && currentSquare != null && isDragging) {
                    currentSquare = (Square) ShapeFactory.createShape(
                            "square", color, lineWidth, fillStyle,
                            new Point[]{new Point(startX, startY, color), new Point(e.getX(), e.getY(), color)});
                    currentSquare.setDragging(false);
                    model.addDrawableWithCommand(currentSquare);
                    currentSquare = null;
                    System.out.println("Added square");
                }
                model.clearCurrentDrawable();
                clearedSelection = false;
                isDragging = false;
            }
        }
    }
}
