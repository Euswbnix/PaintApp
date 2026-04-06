package com.paintapp.paint.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.persistence.SaveVisitor;

/**
 * Representation of for the image.
 * Outlining the how to interact and functionality of ImageS objects.
 */
public class ImageS implements Drawable{
    /**
     * Double object x and y that represents the position of the image.
     * Double object width and height represents the width and height of the image.
     */
    private double x, y, width, height;
    /**
     * Image object stores the image.
     */
    private Image image;
    /**
     * String path stores the image path.
     */
    private String path;

    /**
     * Constructs a new ImageS object.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param image
     * @param path
     */
    public ImageS(double x, double y, double width, double height, Image image, String path) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.path = path;
    }

    /**
     * Gives the image of the ImageS instance
     * @return the image
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Gives the path of the ImageS instance
     * @return the path of ImageS
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Draws the ImageS object.
     * @param g2d GraphicsContext input.
     */
    @Override
    public void draw(GraphicsContext g2d) {
        if (image != null) {
            g2d.drawImage(image, x, y, width, height);
        }
    }

    /**
     * Gives the duplicate of the current ImageS object.
     * @return a duplicate of the current ImageS object.
     */
    @Override
    public Drawable clone() {
        return new ImageS(x, y, width, height, image, path);
    }

    /**
     * Offsets the position of the image.
     * @param dx offset of x-value
     * @param dy offset of y-value
     */
    @Override
    public void offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Gives us true if the point given is within the bounds of the image.
     * @param dx
     * @param dy
     * @return if the point (x,y) is in the image bounds. Else false.
     */
    @Override
    public boolean contains(double dx, double dy) {
        return dx >= x && dx <= (x + width) && dy >= y && dy <= (y + height);
    }

    /**
     * Gives the bounds of the image.
     * @return the bounds of the image.
     */
    @Override
    public double[] getBounds() {
        return new double[]{x, y, width, height};
    }

    /**
     * Changes the color of the image.
     * @param color
     */
    @Override
    public void setColor(Color color) {
    }

    /**
     * Changes the line width of the image
     * @param width
     */
    @Override
    public void setLineWidth(double width) {
    }

    /**
     * Changes the style of the image.
     * @param style
     */
    @Override
    public void setFillStyle(FillStyle style) {
    }

    /**
     * Gives the x position of the image.
     * @return x-value of the position for the image.
     */
    public double getX() {
        return x;
    }

    /**
     * Gives the y position of the image.
     * @return y-value of the position for the image.
     */
    public double getY() {
        return y;
    }

    /**
     * Gives the width of the image.
     * @return width of the image.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gives the height of the image.
     * @return height of the image.
     */
    public double getHeight() {
        return height;
    }

    @Override
    public void accept(SaveVisitor v) {
        v.visit(this);
    }
}
