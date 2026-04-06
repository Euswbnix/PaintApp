package com.paintapp.paint.strategy;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Circle;
import com.paintapp.paint.shapes.Drawable;
import com.paintapp.paint.shapes.Point;
import com.paintapp.paint.shapes.ShapeFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * CircleStrategy handles teh drawing process for the circles. Using MouseEvent to receive the
 * mouse interaction to draw the drawable shapes based on the mouse interactions.
 */
public class CircleStrategy implements DrawingStrategy {
    /**
     * Circle object that represents the current circle
     */
    private Circle currentCircle;
    /**
     * Boolean that is true if the shape is moving, else false.
     */
    private boolean isMoving = false;
    /**
     * Boolean that is true if the shape is being dragged by the mouse, else false.
     */
    private boolean isDragging = false;
    /**
     * Arraylist for all the circles that are being moved.
     */
    private final List<Drawable> moving = new ArrayList<>();
    /**
     * double object lastX, lastY that represents the position of last click
     * from the mouse.
     */
    private double lastX, lastY;
    /**
     * double object pressX, pressY that represents the position of initial click
     * from the mouse.
     */
    private double pressX, pressY;
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
     * Handles the drawing process for drawing circles. When the mouse is first clicked the initial click
     * position is saved and when dragging your mouse the feedback is updated to animate the
     * shape being dragged. Then, when the mouse is released the circle is finalized, the circle is
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
                pressX = e.getX();
                pressY = e.getY();
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
                } else {
                    if (!model.getSelectedShapes().isEmpty()) {
                        model.clearSelection();
                        clearedSelection = true;
                    } else {
                        isMoving = false;
                        clearedSelection = false;
                        currentCircle = null;
                    }
                }
            }
            case "MOUSE_DRAGGED" -> {
                double dragDistance = Math.hypot(e.getX() - pressX, e.getY() - pressY);
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
                    if (currentCircle == null) {
                        currentCircle = (Circle) ShapeFactory.createShape("circle", color, lineWidth, fillStyle,
                                new Point[]{new Point(pressX, pressY, color)});
                    }
                    double dx = Math.abs(e.getX() - currentCircle.getCentre().getX());
                    double dy = Math.abs(e.getY() - currentCircle.getCentre().getY());
                    double radius = Math.hypot(dx, dy);
                    currentCircle.setRadius(radius);
                    model.setCurrentDrawable(currentCircle);
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
                } else if (currentCircle != null && isDragging) {
                    double dx = Math.abs(e.getX() - currentCircle.getCentre().getX());
                    double dy = Math.abs(e.getY() - currentCircle.getCentre().getY());
                    double radius = Math.hypot(dx, dy);
                    currentCircle.setRadius(radius);
                    model.addDrawableWithCommand(currentCircle);
                    currentCircle = null;
                    System.out.println("Added circle");
                }
                model.clearCurrentDrawable();
                clearedSelection = false;
                isDragging = false;
            }
        }
    }
}
