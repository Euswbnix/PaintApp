package com.paintapp.paint.shapes;

import javafx.scene.paint.Color;

/**
 * Representation of the Point Object with getting and setting the characteristics of the point
 * such as the color and the position.
 */
public class Point {
    /**
     * Double x, y that represents the position of the Point.
     */
    public double x, y;
    /**
     * Color object that stores the color of the point.
     */
    public Color color;

    /**
     * Constructs a new Point object with a specific position (x, y) and color.
     * @param x
     * @param y
     * @param color
     */
    public Point(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Constructs a new Point object with a specific position (x, y) and the color as black.
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        this(x, y, Color.BLACK);
    }

    /**
     * Gives the point's x-value attribute
     * @return the point's x-value for its position
     */
    public double getX() {
        return x;
    }

    /**
     * Gives the point's y-value attribute
     * @return the point's y-value for its position
     */
    public double getY() {
        return y;
    }

    /**
     * Gives the point's color attribute
     * @return the point's color for its position
     */
    public Color getColor() {
        return color;
    }
}
