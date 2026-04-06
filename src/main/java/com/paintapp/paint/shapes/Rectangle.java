package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Representation of the Rectangle Object with getting and setting the characteristics of the rectangle
 * such as the color, position, line width, fill style, etc.
 * While outlining how the rectangle is drawn.
 */
public class Rectangle implements Drawable {
    /**
     * Double object x and y that represents the position of the rectangle.
     * Double object width and height represents the width and height of
     * the rectangle.
     */
    private double x, y, width, height;
    /**
     * Color object that stores the color of the rectangle.
     */
    private Color color;
    /**
     * double object that represents the line width of the rectangle.
     */
    private double lineWidth;
    /**
     * Fill style object that stores the fill style of the rectangle.
     */
    private FillStyle fillStyle;

    /**
     * Constructs a new Rectangle object with a specific position, width, height, color, line width,
     * and fill style.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param color
     * @param lineWidth
     * @param fillStyle
     */
    public Rectangle(double x, double y, double width, double height, Color color, double lineWidth, FillStyle fillStyle) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.lineWidth = lineWidth;
        this.fillStyle = fillStyle;
    }

    /**
     * RETURNS a new Rectangle with the given inputs as its characteristics.
     * @param x
     * @param x2
     * @param y
     * @param y2
     * @param color
     * @param lineWidth
     * @param f
     * @return new Rectangle object
     */
    public static Rectangle CreateRectangle(double x, double x2, double y, double y2, Color color, double lineWidth, FillStyle f) {
        double new_x = Math.min(x, x2);
        double new_y = Math.min(y, y2);
        double new_width = Math.abs(x - x2);
        double new_height = Math.abs(y - y2);

        return new Rectangle(new_x, new_y, new_width, new_height, color, lineWidth, f);
    }

    /**
     * RETURNS the x-value of the position for the rectangle.
     * @return x-value of the position for the rectangle.
     */
    public double getX() {
        return this.x;
    }

    /**
     * RETURNS the y-value of the position for the rectangle.
     * @return y-value of the position for the rectangle.
     */
    public double getY() {
        return this.y;
    }

    /**
     * RETURNS the width of the position for the rectangle.
     * @return width  of the position for the rectangle.
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * RETURNS the height of the position for the rectangle.
     * @return height of the position for the rectangle.
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * RETURNS the color of the position for the rectangle.
     * @return color of the position for the rectangle.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Changes the x-value of the position of the rectangle.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Changes the y-value of the position of the rectangle.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Changes the width of the rectangle.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * RETURNS the line width of the rectangle.
     * @return color of the line width of the rectangle.
     */
    public double getLineWidth() {
        return this.lineWidth;
    }

    /**
     * RETURNS the fill style of the rectangle.
     * @return color of the fill style of the rectangle.
     */
    public FillStyle getFillStyle() {
        return this.fillStyle;
    }

    /**
     * Changes the color of the rectangle.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Draws the rectangle object while considering the fill style of the rectangle.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        g2d.setFill(color);
        g2d.setStroke(color);
        g2d.setLineWidth(lineWidth);
        if (fillStyle == FillStyle.FILLED) {
            g2d.fillRect(x, y, width, height);
        } else {
            g2d.strokeRect(x, y, width, height);
        }

    }

    /**
     * Clones the rectangle circle.
     * @return a duplicate of the current rectangle.
     */
    @Override
    public Drawable clone() {
        return new Rectangle(x, y, width, height, color, lineWidth, fillStyle);
    }

    /**
     * Offsets the position of the rectangle.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns true if the point is within the rectangle. Otherwise, false.
     * @param dx x-value of a point
     * @param dy y-value of a point
     * @return true if the point is within the rectangle. Otherwise, false.
     */
    @Override
    public boolean contains(double dx, double dy) {
        return dx >= x && dx <= x + width && dy >= y && dy <= y + height;
    }

    /**
     * Gives the bounds of the rectangle.
     * @return the bounds of the rectangle.
     */
    @Override
    public double[] getBounds() {
        return new double[]{x, y, width, height};
    }

    /**
     * Changes the line width of the rectangle to the given x-value.
     */
    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    /**
     * Changes the fill style of the rectangle to the given x-value.
     */
    @Override
    public void setFillStyle(FillStyle style) {
        this.fillStyle = style;
    }
}