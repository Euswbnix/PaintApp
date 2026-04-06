package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Interface for the drawable shapes.
 * Outlining the how to interact and functionality of the
 * drawable shapes.
 */
public interface Drawable {
    /**
     * Draws the drawable shape.
     * @param g2d
     */
    void draw(GraphicsContext g2d);

    /**
     * Gives a duplicate of the shape.
     * @return shape with the same attributes as the current instance shape.
     */
    Drawable clone();

    /**
     * Offsets the change
     * @param dx
     * @param dy
     */
    void offset(double dx, double dy);

    /**
     * Gives us true if the point given is within the bounds of the shape.
     * @param x
     * @param y
     * @return if the point (x,y) is in the shape. Else false.
     */
    boolean contains(double x, double y);

    /**
     * Gives the bounds of the shape.
     * @return the bounds of the shape.
     */
    double[] getBounds();

    /**
     * Changes the color of the shape.
     * @param color
     */
    void setColor(Color color);

    /**
     * Changes the line width of the shape
     * @param lineWidth
     */
    void setLineWidth(double lineWidth);

    /**
     * Changes the style of the shape
     * @param style
     */
    void setFillStyle(FillStyle style);
}
