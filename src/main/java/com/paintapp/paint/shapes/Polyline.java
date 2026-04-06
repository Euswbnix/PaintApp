package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.persistence.SaveVisitor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of the Polyline Object with getting and setting the characteristics of the polyline
 * such as the color and line width.
 * While outlining how the polyline is drawn.
 */
public class Polyline implements Drawable {
    /**
     * ArrayList that contains all the points in the polyline.
     */
    private final List<Point> points = new ArrayList<>();
    /**
     * Color object that stores the color of the polyline.
     */
    private Color color;
    /**
     * double object that represents the line width of the polyline.
     */
    private double lineWidth;
    /**
     * Point object that represents the last point in the polyline.
     */
    private Point previewPoint = null;

    /**
     *  Constructs a new Polyline object with a specific color and line width.
     * @param color
     * @param lineWidth
     */
    public Polyline(Color color, double lineWidth) {
        this.color = color;
        this.lineWidth = lineWidth;
    }

    /**
     * Adds the point to points ArrayList
     * @param p
     */
    public void addPoint(Point p) {
        if (p != null) points.add(p);
    }

    /**
     * Gives the length of the points ArrayList
     * @return size of points ArrayList.
     */
    public int size() {
        return points.size();
    }

    /**
     * Changes the current preview point to point p.
     * @param p
     */
    public void setPreview(Point p) {
        this.previewPoint = p;
    }

    /**
     * Gives the distance from the line segments.
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return the distance from the line segments.
     */
    private double distanceToLineSegment(double x, double y, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lengthSquared = dx * dx + dy * dy;
        if (lengthSquared == 0) {
            return Math.hypot(x - x1, y - y1);
        }
        double t = Math.max(0, Math.min(1, ((x - x1) * dx + (y - y1) * dy) / lengthSquared));
        double pX = x1 + t * dx;
        double pY = y1 + t * dy;
        return Math.hypot(pX - x, pY - y);
    }

    /**
     * Draws the Polyline object.
     * @param g GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g) {
        if (g == null) return;
        g.setStroke(color);
        g.setLineWidth(lineWidth);
        for (int i = 0; i + 1 < points.size(); i++) {
            Point a = points.get(i), b = points.get(i + 1);
            g.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
        if (!points.isEmpty() && previewPoint != null) {
            g.setLineDashes(8);
            Point last = points.get(points.size() - 1);
            g.strokeLine(last.getX(), last.getY(), previewPoint.getX(), previewPoint.getY());
            g.setLineDashes();
        }
    }

    /**
     * Gives a duplicate of the polyline
     * @return polyline with the same attributes as the current instance polyline.
     */
    @Override
    public Drawable clone() {
        Polyline p = new Polyline(color, lineWidth);
        for (Point point : points) {
            p.addPoint(new Point(point.getX(), point.getY(), point.getColor()));
        }
        return p;
    }

    /**
     * Offsets the position of the polyline
     * @param dx
     * @param dy
     */
    @Override
    public void offset(double dx, double dy) {
        List<Point> newPoints = new ArrayList<>();
        for (Point p : points) {
            newPoints.add(new Point(p.getX() + dx, p.getY() + dy, p.getColor()));
        }
        points.clear();
        points.addAll(newPoints);
    }

    /**
     * RETURN true if the point is in the bounds of the polyline
     * @param dx
     * @param dy
     * @return true if the point is in the bounds of the polyline
     */
    @Override
    public boolean contains(double dx, double dy) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            double d = distanceToLineSegment(dx, dy, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (d <= lineWidth + 5) {
                return true;
            }
        }
        return false;
    }

    /**
     * RETURNS the bounds of the polyline. Where the minX and MinY are the smallest x and y value.
     * And maxX and maxY are the largest x and y value to contain the entire polyline.
     * @return the bounds of the polyline
     */
    @Override
    public double[] getBounds() {
        if (points.isEmpty()) {
            return new double[]{0, 0, 0, 0};
        }
        double minX = points.get(0).getX();
        double minY = points.get(0).getY();
        double maxX = minX;
        double maxY = minY;
        for (Point p : points) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }
        return new double[]{minX - 5, minY - 5, maxX - minX + 10, maxY - minY + 10};
    }

    /**
     * Changes the color of the polyline.
     * @param color
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the width of the polyline.
     * @param width
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Changes the fill Style of the polyline.
     * @param fillStyle
     */
    @Override
    public void setFillStyle(FillStyle fillStyle) {
    }

    /**
     * @return the polyline's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return the polyline's line width
     */
    public double getLineWidth() {
        return this.lineWidth;
    }

    /**
     * @return an unmodifiable view of the polyline's points
     */
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    @Override
    public void accept(SaveVisitor v) {
        v.visit(this);
    }
}
