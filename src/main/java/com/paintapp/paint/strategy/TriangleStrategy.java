package com.paintapp.paint.strategy;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;
import com.paintapp.paint.shapes.Point;
import com.paintapp.paint.shapes.ShapeFactory;
import com.paintapp.paint.shapes.Triangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class TriangleStrategy implements DrawingStrategy {
    /**
     * Arraylists that contains the points of the triangle.
     */
    private final ArrayList<Point> points = new ArrayList<>();
    /**
     * Boolean that is true if the shape is being dragged by the mouse, else false.
     */
    private boolean isMoving = false;
    /**
     * Arraylist for all the triangles that are being moved.
     */
    private final List<Drawable> moving = new ArrayList<>();
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
     * Handles the drawing process for drawing triangle. When the mouse is first clicked the initial click
     * position is saved and when dragging your mouse the feedback is updated to animate the
     * shape being dragged. Then, when the mouse is released the triangle is finalized, the triangle is
     * added to the program screen.
     * @param e
     * @param model
     * @param color
     * @param lineWidth
     * @param fillStyle
     */
    @Override
    public void handle(MouseEvent e, PaintModel model, Color color, double lineWidth, FillStyle fillStyle) {
        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if (points.isEmpty()) {
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
                        return;
                    } else {
                        model.selectShapeAt(e.getX(), e.getY());
                        clearedSelection = false;
                        return;
                    }
                } else {
                    if (!model.getSelectedShapes().isEmpty()) {
                        model.clearSelection();
                        clearedSelection = true;
                        return;
                    }
                }
            }
            if (!isMoving && !clearedSelection) {
                double x = e.getX();
                double y = e.getY();
                points.add(new Point(x, y));
                Drawable preview = new Drawable() {
                    private final List<Point> previewPoints = new ArrayList<>(points);

                    @Override
                    public void draw(GraphicsContext gc) {
                        gc.setFill(color);
                        for (Point p : previewPoints) {
                            gc.fillOval(p.getX() - 3, p.getY() - 3, 6, 6);
                        }
                        if (previewPoints.size() >= 2) {
                            gc.setStroke(color);
                            gc.setLineWidth(1);
                            gc.setLineDashes(5);
                            for (int i = 0; i < previewPoints.size() - 1; i++) {
                                gc.strokeLine(previewPoints.get(i).getX(), previewPoints.get(i).getY(),
                                        previewPoints.get(i + 1).getX(), previewPoints.get(i + 1).getY());
                            }
                            gc.setLineDashes();
                        }
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

                if (points.size() < 3) {
                    model.setCurrentDrawable(preview);
                } else {
                    model.clearCurrentDrawable();
                    Triangle t = (Triangle) ShapeFactory.createShape(
                            "triangle", color, lineWidth, fillStyle,
                            new Point[]{points.get(0), points.get(1), points.get(2)});
                    model.addDrawableWithCommand(t);
                    points.clear();
                    System.out.println("Added triangle");
                }
            }
        } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
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
            }
        } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (isMoving) {
                if (Math.abs(totalDX) > 0.1 || Math.abs(totalDY) > 0.1) {
                    for (Drawable d : moving) d.offset(-totalDX, -totalDY);
                    model.moveUpdate();
                    model.moveSelectedBy(totalDX, totalDY);
                }
                moving.clear();
                isMoving = false;
            }
            clearedSelection = false;
        }
    }
}