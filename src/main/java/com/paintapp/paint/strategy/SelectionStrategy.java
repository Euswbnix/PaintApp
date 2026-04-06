package com.paintapp.paint.strategy;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectionStrategy handles the drawing process for displaying a shape is selected.
 * Using MouseEvent to receive the mouse interaction to draw the drawable shapes
 * based on the mouse interactions.
 */
public class SelectionStrategy implements DrawingStrategy {
    /**
     * Arraylist for all the rectangles that are being moved.
     */
    private final List<Drawable> moving = new ArrayList<>();
    /**
     * Boolean that is true if the shape is being dragged by the mouse, else false.
     */
    private boolean dragging = false;
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
     * boolean that represents if the selection box is being drawn, else flase.
     */
    private boolean drawingSelectionBox = false;
    /**
     * Initial position, selectionStartX, selectionStartY, for the selection area
     */
    private double selectionStartX, selectionStartY;
    /**
     * Final position, selectionEndX, selectionEndY, for the selection area
     */
    private double selectionEndX, selectionEndY;

    /**
     * Handles the drawing process for drawing selection on each shape that is selected.
     * When the mouse is first clicked the initial click position is saved and when dragging
     * your mouse the feedback is updated to animate the selection on the shape being dragged.
     * Then, when the mouse is released the selection disappears and the shape remains.
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
                List<Drawable> currentSelection = model.getSelectedShapes();
                for (Drawable selected : currentSelection) {
                    if (selected.contains(e.getX(), e.getY())) {
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
                if (hit != null && currentSelection.contains(hit)) {
                    dragging = true;
                    moving.clear();
                    moving.addAll(currentSelection);
                    lastX = e.getX();
                    lastY = e.getY();
                    totalDX = totalDY = 0.0;
                    drawingSelectionBox = false;
                } else if (hit != null) {
                    model.selectShapeAt(e.getX(), e.getY());
                    dragging = false;
                    drawingSelectionBox = false;
                } else {
                    model.clearSelection();
                    dragging = false;
                    drawingSelectionBox = true;
                    selectionStartX = e.getX();
                    selectionStartY = e.getY();
                    selectionEndX = e.getX();
                    selectionEndY = e.getY();
                }
            }
            case "MOUSE_DRAGGED" -> {
                if (dragging) {
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
                } else if (drawingSelectionBox) {
                    selectionEndX = e.getX();
                    selectionEndY = e.getY();
                    Drawable selectionBoxPreview = new Drawable() {
                        @Override
                        public void draw(GraphicsContext gc) {
                            double x = Math.min(selectionStartX, selectionEndX);
                            double y = Math.min(selectionStartY, selectionEndY);
                            double w = Math.abs(selectionEndX - selectionStartX);
                            double h = Math.abs(selectionEndY - selectionStartY);

                            gc.setStroke(Color.DODGERBLUE);
                            gc.setLineWidth(1);
                            gc.setLineDashes(5, 5);
                            gc.strokeRect(x, y, w, h);
                            gc.setLineDashes();
                            gc.setFill(Color.color(0.3, 0.6, 1.0, 0.1));
                            gc.fillRect(x, y, w, h);
                        }

                        @Override
                        public Drawable clone() {
                            return null;
                        }

                        @Override
                        public void offset(double dx, double dy) {
                        }

                        @Override
                        public boolean contains(double dx, double dy) {
                            return false;
                        }

                        @Override
                        public double[] getBounds() {
                            return new double[]{0, 0, 0, 0};
                        }

                        @Override
                        public void setColor(Color color) {
                        }

                        @Override
                        public void setLineWidth(double width) {
                        }

                        @Override
                        public void setFillStyle(FillStyle style) {
                        }
                    };

                    model.setCurrentDrawable(selectionBoxPreview);
                }
            }
            case "MOUSE_RELEASED" -> {
                if (dragging) {
                    if (Math.abs(totalDX) > 0.1 || Math.abs(totalDY) > 0.1) {
                        for (Drawable d : moving) d.offset(-totalDX, -totalDY);
                        model.moveUpdate();
                        model.moveSelectedBy(totalDX, totalDY);
                    }
                    moving.clear();
                    dragging = false;
                } else if (drawingSelectionBox) {
                    double x1 = Math.min(selectionStartX, selectionEndX);
                    double y1 = Math.min(selectionStartY, selectionEndY);
                    double x2 = Math.max(selectionStartX, selectionEndX);
                    double y2 = Math.max(selectionStartY, selectionEndY);
                    List<Drawable> toSelect = new ArrayList<>();
                    for (Drawable d : model.getDrawables()) {
                        double[] bounds = d.getBounds();
                        double shapeX = bounds[0];
                        double shapeY = bounds[1];
                        double shapeW = bounds[2];
                        double shapeH = bounds[3];
                        boolean intersects = !(shapeX + shapeW < x1 ||
                                shapeX > x2 ||
                                shapeY + shapeH < y1 ||
                                shapeY > y2);

                        if (intersects) {
                            toSelect.add(d);
                        }
                    }
                    model.selectMultipleShapes(toSelect);
                    model.clearCurrentDrawable();
                    drawingSelectionBox = false;
                }
            }
        }
    }
}