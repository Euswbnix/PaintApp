package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.persistence.SaveVisitor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Representation of the Circle Object with getting and setting the characteristics of the circle
 * such as the color, centre, line width, radius, and fill style.
 * While outlining how the circle is drawn.
 */
public class Circle implements Drawable {
    /**
     * Color object that stores the color of the circle.
     */
    private Color color;
    /**
     * Point object that stores the centre of the circle.
     */
    private Point centre;
    /**
     * Radius object that stores the radius of the circle.
     */
    private double radius;
    /**
     * Line width object that stores the line width of the circle.
     */
    private double lineWidth;
    /**
     * Fill style object that stores the fill style of the circle.
     */
    private FillStyle fillStyle;

    /**
     * Constructs a new Circle object with a specific centre, radius, color, line width, and fill style.
     * @param centre
     * @param radius
     * @param color
     * @param lineWidth
     * @param fillStyle
     */
    public Circle(Point centre, double radius, Color color, double lineWidth, FillStyle fillStyle) {
        this.centre = centre;
        this.radius = radius;
        this.color = color;
        this.lineWidth = lineWidth;
        this.fillStyle = fillStyle;
    }

    /**
     * Gives the circle's color attribute
     * @return the circle's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Gives the circle's centre attribute
     * @return the circle's centre
     */
    public Point getCentre() {
        return centre;
    }

    /**
     * Gives the circle's line width attribute
     * @return the circle's line width
     */
    public double getLineWidth() {
        return this.lineWidth;
    }

    /**
     * Gives the circle's  fill style attribute
     * @return the circle's fill style
     */
    public FillStyle getFillStyle() {
        return this.fillStyle;
    }

    /**
     * Changes the circle's radius to the inputted radius
     * @param radius of the circle
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Gives the circle's radius attribute
     * @return the circle's radius
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Draws the circle object while considering the fill style of the circle.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        g2d.setFill(color);
        g2d.setStroke(color);
        g2d.setLineWidth(lineWidth);
        if (fillStyle == FillStyle.FILLED) {
            g2d.fillOval(centre.x - radius, centre.y - radius, radius * 2, radius * 2);
        } else {
            g2d.strokeOval(centre.x - radius, centre.y - radius, radius * 2, radius * 2);
        }

    }

    /**
     * Clones the current circle.
     * @return a duplicate of the current circle
     */
    @Override
    public Drawable clone() {
        return new Circle(new Point(centre.x, centre.y, color), radius, color, lineWidth, fillStyle);
    }

    /**
     * Offsets the centre of the circle.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        centre = new Point(centre.x + dx, centre.y + dy, centre.color);
    }

    /**
     * Returns true if the point is within the circle. Otherwise, false.
     * @param x x-value of a point
     * @param y y-value of a point
     * @return true if the distance of the point at (x, y) is less than or equal to
     * the radius. Otherwise, false.
     */
    @Override
    public boolean contains(double x, double y) {
        double dx = x - centre.x;
        double dy = y - centre.y;
        return Math.hypot(dx, dy) <= radius;
    }

    /**
     * Gives the bounds of the circle
     * @return the bounds of the circle
     */
    @Override
    public double[] getBounds() {
        return new double[]{centre.x - radius, centre.y - radius, radius * 2, radius * 2};
    }

    /**
     * Changes the color of the circle
     * @param color
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the width of the circle
     * @param width
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Changes the fill style of the circle
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
