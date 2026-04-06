package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.persistence.SaveVisitor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of the Squiggle Object with getting and setting the characteristics of the squiggle
 * such as the color, centre, line width, etc.
 * While outlining how the squiggle is drawn.
 */
public class Squiggle implements Drawable {
    /**
     * ArrayList that contains all the points in the squiggle.
     */
    private ArrayList<Point> points = new ArrayList<>();
    /**
     * Color object that stores the color of the squiggle.
     */
    private Color color;
    /**
     * double object that represents the line width of the squiggle.
     */
    private double lineWidth;

    /**
     * Constructs a new Squiggle object with a specific color and line width.
     * @param color
     * @param lineWidth
     */
    public Squiggle(Color color, double lineWidth) {
        this.color = color;
        this.lineWidth = lineWidth;
    }

    /**
     * Adds the point to points ArrayList
     * @param p
     */
    public void addPoint(Point p) {
        points.add(p);
    }

    /**
     * RETURNS the color attribute of the squiggle.
     * @return the color of the squiggle
     */
    public Color getColor() {
        return color;
    }

    /**
     * RETURNS the line width of the squiggle.
     * @return the lineWidth attribute of the squiggle
     */
    public double getLineWidth() {
        return lineWidth;
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
        return Math.hypot(x - pX, y - pY);
    }

    /**
     * Draws the squiggle object.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        if (points.size() < 2) {
            return;
        }
        g2d.setStroke(color);
        g2d.setLineWidth(lineWidth);
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g2d.strokeLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    /**
     * Gives a duplicate of the squiggle
     * @return Squiggle object with the same attributes as the current instance squiggle.
     */
    @Override
    public Drawable clone() {
        Squiggle s = new Squiggle(color, lineWidth);
        for (Point p : points) {
            s.addPoint(new Point(p.x, p.y, p.color));
        }
        return s;
    }

    /**
     * Offsets the position of the squiggle.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        ArrayList<Point> newPoints = new ArrayList<>();
        for (Point p : points) {
            newPoints.add(new Point(p.x + dx, p.y + dy, p.color));
        }
        points = newPoints;
    }

    /**
     * Returns true if the point is within the squiggle. Otherwise, false.
     * @param dx x-value of a point
     * @param dy y-value of a point
     * @return true if the point is within the squiggle. Otherwise, false.
     */
    @Override
    public boolean contains(double dx, double dy) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            double dist = distanceToLineSegment(dx, dy, p1.x, p1.y, p2.x, p2.y);
            if (dist <= lineWidth + 5) {
                return true;
            }
        }
        return false;
    }

    /**
     * RETURNS the bounds of the squiggle. Where the minX and MinY are the smallest x and y value.
     * And maxX and maxY are the largest x and y value to contain the entire squiggle.
     * @return the bounds of the squiggle
     */
    @Override
    public double[] getBounds() {
        if (points.isEmpty()) {
            return new double[]{0, 0, 0, 0};
        }
        double minX = points.get(0).x;
        double minY = points.get(0).y;
        double maxX = minX;
        double maxY = minY;
        for (Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }
        return new double[]{minX - 5, minY - 5, maxX - minX + 10, maxY - minY + 10};
    }

    /**
     * Changes color to the color given of the squiggle.
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes line width to the color given of the squiggle.
     * @param width
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Unimplemented setFillStyle from the interface Drawable
     * @param fillStyle
     */
    @Override
    public void setFillStyle(FillStyle fillStyle) {
    }

    /**
     * @return an unmodifiable view of the squiggle's points
     */
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    @Override
    public void accept(SaveVisitor v) {
        v.visit(this);
    }
}
