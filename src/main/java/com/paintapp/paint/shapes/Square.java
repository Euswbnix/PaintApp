package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Representation of the Square Object with getting and setting the characteristics of the square
 * such as the color, position, line width, fill style, etc.
 * While outlining how the square is drawn.
 */
public class Square implements Drawable {
    /**
     * Double object x and y that represents the position of the square.
     * Double object size that represents the width and height of
     * the square.
     */
    private double x, y, size;
    /**
     * Color object that stores the color of the square.
     */
    private Color color;
    /**
     * double object that represents the line width of the square.
     */
    private double lineWidth;
    /**
     * Boolean that states if the square is being drawn. This is the case if the mouse
     * is dragging the cursor to draw the square.
     */
    private boolean dragging = false;
    /**
     * Fill style object that stores the fill style of the square.
     */
    private FillStyle fillStyle;

    /**
     * Constructs a new Square object with a specific position, size, color, line width,
     * and fill style.
     * @param x
     * @param y
     * @param size
     * @param color
     * @param lineWidth
     * @param f
     */
    public Square(double x, double y, double size, Color color, double lineWidth, FillStyle f) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        this.lineWidth = lineWidth;
        this.fillStyle = f;
    }

    /**
     * RETURNS the x-value of the position for the square.
     * @return x-value of the position for the square.
     */
    public double getX() {
        return this.x;
    }

    /**
     * RETURNS the y-value of the position for the square.
     * @return y-value of the position for the square.
     */
    public double getY() {
        return this.y;
    }

    /**
     * RETURNS the size for the square.
     * @return size for the square.
     */
    public double getSize() {
        return this.size;
    }

    /**
     * RETURNS the color for the square.
     * @return color for the square.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * RETURNS the line width for the square.
     * @return line width for the square.
     */
    public double getLineWidth() {
        return this.lineWidth;
    }

    /**
     * RETURNS the fill Style for the square.
     * @return fill Style for the square.
     */
    public FillStyle getFillStyle() {
        return this.fillStyle;
    }

    /**
     * Sets true if we are dragging the square while drawing or not.
     * @param drag true if the square is being drawn or not.
     */
    public void setDragging(boolean drag) {
        this.dragging = drag;
    }

    /**
     * Creates a new Square object based on the characteristics given.
     * @param x
     * @param x2
     * @param y
     * @param y2
     * @param color
     * @param lineWidth
     * @param f
     * @return new Square object.
     */
    public static Square CreateSquare(double x, double x2, double y, double y2, Color color, double lineWidth, FillStyle f) {
        double sideLength = Math.min(Math.abs(x2 - x), Math.abs(y2 - y));
        double new_x;
        double new_y;

        if (x2 >= x) {
            new_x = x;
        } else {
            new_x = x - sideLength;
        }
        if (y2 >= y) {
            new_y = y;
        } else {
            new_y = y - sideLength;
        }

        return new Square(new_x, new_y, sideLength, color, lineWidth, f);
    }

    /**
     * Draws the rectangle object while considering the fill style of the rectangle.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        if (dragging) {
            g2d.setStroke(color);
            g2d.setLineDashes(4);
            g2d.strokeRect(x, y, size, size);
            g2d.setLineDashes(0);
        } else {
            g2d.setFill(color);
            g2d.setStroke(color);
            g2d.setLineWidth(lineWidth);
            if (fillStyle == FillStyle.FILLED) {
                g2d.fillRect(x, y, size, size);
            } else {
                g2d.strokeRect(x, y, size, size);
            }

        }
    }

    /**
     * Clones the current square.
     * @return a duplicate of the current square
     */
    @Override
    public Square clone() {
        return new Square(x, y, size, color, lineWidth, fillStyle);
    }

    /**
     * Offsets the position of the square.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns true if the point is within the square. Otherwise, false.
     * @param dx x-value of a point
     * @param dy y-value of a point
     * @return true if the point is within the square. Otherwise, false.
     */
    @Override
    public boolean contains(double dx, double dy) {
        return dx >= x && dx <= x + size && dy >= y && dy <= y + size;
    }

    /**
     * Gives the bounds of the square.
     * @return the bounds of the square.
     */
    @Override
    public double[] getBounds() {
        return new double[]{x, y, size, size};
    }

    /**
     * Changes the color of the square to the color given.
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Changes the line width of the square to the line width given.
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Changes the fill style of the square to the fill style given.
     */
    @Override
    public void setFillStyle(FillStyle style) {
        this.fillStyle = style;
    }
}
