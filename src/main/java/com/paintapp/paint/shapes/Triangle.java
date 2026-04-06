package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.persistence.SaveVisitor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Representation of the Triangle Object with getting and setting the characteristics of the triangle
 * such as the color, centre, line width, etc.
 * While outlining how the triangle is drawn.
 */
public class Triangle implements Drawable {
    /**
     * Points a, b, c represents the three points on a triangle
     */
    private Point a, b, c;
    /**
     * Color object that stores the color of the triangle.
     */
    private Color color;
    /**
     * double object that represents the line width of the triangle.
     */
    private double lineWidth;
    /**
     * Fill style object that stores the fill style of the triangle.
     */
    private FillStyle fillStyle;

    /**
     * Constructs a new Triangle object with a specific color, line width, etc.
     * @param a
     * @param b
     * @param c
     * @param color
     * @param lineWidth
     * @param f
     */
    public Triangle(Point a, Point b, Point c, Color color, double lineWidth, FillStyle f) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = color;
        this.lineWidth = lineWidth;
        this.fillStyle = f;
    }

    /**
     * RETURNS the point a on the triangle
     * @return a
     */
    public Point getA() {
        return a;
    }

    /**
     * RETURNS the point b on the triangle
     * @return b
     */
    public Point getB() {
        return b;
    }

    /**
     * RETURNS the point c on the triangle
     * @return c
     */
    public Point getC() {
        return c;
    }

    /**
     * RETURNS the line width of the triangle.
     * @return the lineWidth attribute of the triangle
     */
    public double getLineWidth() {
        return lineWidth;
    }

    /**
     * RETURNS the color of the triangle.
     * @return the color  attribute of the triangle
     */
    public Color getColor() {
        return color;
    }

    /**
     * RETURNS the fill style of the triangle.
     * @return the fillStyle attribute of the triangle
     */
    public FillStyle getFillStyle() {
        return fillStyle;
    }

    /**
     * Draws the triangle object while considering the fill style of the triangle.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        double[] xPoints = {a.x, b.x, c.x};
        double[] yPoints = {a.y, b.y, c.y};
        g2d.setStroke(color);
        g2d.setFill(color);
        g2d.setLineWidth(lineWidth);
        if (fillStyle == FillStyle.FILLED) {
            g2d.fillPolygon(xPoints, yPoints, 3);
        } else {
            g2d.strokePolygon(xPoints, yPoints, 3);
        }
    }

    /**
     * Clones the current triangle.
     * @return a duplicate of the current triangle.
     */
    @Override
    public Drawable clone() {
        return new Triangle(new Point(a.x, a.y, a.color),
                new Point(b.x, b.y, b.color),
                new Point(c.x, c.y, c.color),
                color, lineWidth, fillStyle);
    }

    /**
     * Offsets the position of the triangle.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        a = new Point(a.x + dx, a.y + dy, a.color);
        b = new Point(b.x + dx, b.y + dy, b.color);
        c = new Point(c.x + dx, c.y + dy, c.color);
    }

    /**
     * Returns true if the point is within the triangle. Otherwise, false.
     * @param dx x-value of a point
     * @param dy y-value of a point
     * @return true if the point is within the triangle. Otherwise, false.
     */
    @Override
    public boolean contains(double dx, double dy) {
        double d = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y);
        double alpha = ((b.y - c.y) * (dx - c.x) + (c.x - b.x) * (dy - c.y)) / d;
        double beta = ((c.y - a.y) * (dx - c.x) + (a.x - c.x) * (dy - c.y)) / d;
        double gamma = 1.0 - alpha - beta;
        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }

    /**
     * RETURNS the bounds of the triangle. Where the minX and MinY are the smallest x and y value.
     * And maxX and maxY are the largest x and y value to contain the entire triangle.
     * @return the bounds of the triangle
     */
    @Override
    public double[] getBounds() {
        return new double[]{Math.min(a.x, Math.min(b.x, c.x)), Math.min(a.y, Math.min(b.y, c.y)),
                Math.max(a.x, Math.max(b.x, c.x)) - Math.min(a.x, Math.min(b.x, c.x)),
                Math.max(a.y, Math.max(b.y, c.y)) - Math.min(a.y, Math.min(b.y, c.y))};
    }

    /**
     * Changes the color attribute of the triangle to the color given.
     * @param color
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the color attribute of the triangle to the color given.
     * @param width
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Changes the fill style attribute of the triangle to the fill style given.
     * @param style
     */
    @Override
    public void setFillStyle(FillStyle style) {
        this.fillStyle = style;
    }

    @Override
    public void accept(SaveVisitor v) {
        v.visit(this);
    }
}
