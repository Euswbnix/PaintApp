package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.persistence.SaveVisitor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Representation of the Oval Object with getting and setting the characteristics of the oval
 * such as the color, centre, line width, fill style, etc.
 * While outlining how the oval is drawn.
 */
public class Oval implements Drawable {
    /**
     * Double object x and y that represents the position of the oval.
     * Double object width and height represents the width and height of the oval.
     */
    private double x, y, width, height;
    /**
     * Color object that stores the color of the oval.
     */
    private Color color;
    /**
     * Line width object that stores the line width of the line width.
     */
    private double lineWidth;
    /**
     * Boolean that states if the oval is being drawn. This is the case if the mouse
     * is dragging the cursor to draw the oval.
     */
    private boolean dragging = false;
    /**
     * Fill style object that stores the fill style of the oval.
     */
    private FillStyle fillStyle;

    /**
     * Constructs a new Oval object with a specific position, width, height, color, line width, and fill style.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param color
     * @param lineWidth
     * @param f
     */
    public Oval(double x, double y, double width, double height, Color color, double lineWidth, FillStyle f) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.lineWidth = lineWidth;
        this.fillStyle = f;
    }

    /**
     * Gives the oval's x-value attribute
     * @return the oval's x-value for its position
     */
    public double getX() {
        return x;
    }

    /**
     * Gives the oval's y-value attribute
     * @return the oval's y-value for its position
     */
    public double getY() {
        return y;
    }

    /**
     * Gives the oval's width attribute
     * @return the oval's width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gives the oval's height attribute
     * @return the oval's height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gives the oval's line width attribute
     * @return the oval's line width
     */
    public double getLineWidth() {
        return lineWidth;
    }

    /**
     * Gives the oval's fill style attribute
     * @return the oval's fill style
     */
    public FillStyle getFillStyle() {
        return fillStyle;
    }

    /**
     * Gives the oval's color attribute
     * @return the oval's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets true if we are dragging the oval while drawing or not.
     * @param drag true if the oval is being drawn or not.
     */
    public void setDragging(boolean drag) {
        this.dragging = drag;
    }

    /**
     * Creates and returns a new Oval object based on the characteristics given.
     * @param x
     * @param x2
     * @param y
     * @param y2
     * @param color
     * @param lineWidth
     * @param f
     * @return new Oval object
     */
    public static Oval createOval(double x, double x2, double y, double y2, Color color, double lineWidth, FillStyle f) {
        double new_x = Math.min(x, x2);
        double new_y = Math.min(y, y2);
        double width = Math.abs(x2 - x);
        double height = Math.abs(y2 - y);
        return new Oval(new_x, new_y, width, height, color, lineWidth, f);
    }

    /**
     * Draws the oval object while considering the fill style of the circle.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        if (dragging) {
            g2d.setStroke(color);
            g2d.setLineDashes(4);
            g2d.strokeOval(x, y, width, height);
            g2d.setLineDashes(0);
        } else {
            g2d.setFill(color);
            g2d.setStroke(color);
            g2d.setLineWidth(lineWidth);
            if (fillStyle == FillStyle.FILLED) {
                g2d.fillOval(x, y, width, height);
            } else {
                g2d.strokeOval(x, y, width, height);
            }
        }
    }

    /**
     * Clones the current oval.
     * @return a duplicate of the current oval
     */
    @Override
    public Drawable clone() {
        return new Oval(x, y, width, height, color, lineWidth, fillStyle);
    }

    /**
     * Offsets the position of the oval.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns true if the point is within the oval. Otherwise, false.
     * @param dx x-value of a point
     * @param dy y-value of a point
     * @return true if the point is within the oval. Otherwise, false.
     */
    @Override
    public boolean contains(double dx, double dy) {
        double cx = x + width / 2;
        double cy = y + height / 2;
        double nX = (dx - cx) / (width / 2);
        double nY = (dy - cy) / (height / 2);
        return nX * nX + nY * nY <= 1;
    }

    /**
     * Gives the bounds of the oval.
     * @return the bounds of the oval.
     */
    @Override
    public double[] getBounds() {
        return new double[]{x, y, width, height};
    }

    /**
     * Changes the color of the oval.
     * @param color
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the line width of the oval.
     * @param width
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Changes the fill style of the oval.
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
