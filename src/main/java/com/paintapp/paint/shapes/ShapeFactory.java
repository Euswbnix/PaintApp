package com.paintapp.paint.shapes;

import com.paintapp.paint.app.FillStyle;
import javafx.scene.paint.Color;

/**
 * ShapeFactory class is the representation of a shape factory that creates the specified shape.
 * Centralizing the shape creation process into one class, that separates shape creation process
 * from the strategy classes.
 */
public class ShapeFactory {
    /**
     * A generalized function that purpose is to create the specified type's shape.
     * Separating the shape creation process from the strategy classes.
     * @param type
     * @param color
     * @param lineWidth
     * @param fillStyle
     * @param pts
     * @return
     */
    public static Drawable createShape(String type, Color color, double lineWidth, FillStyle fillStyle, Point[] pts) {
        if (type.equalsIgnoreCase("circle")) {
            if (pts != null && pts.length >= 1 && pts[0] != null) {
                return new Circle(pts[0], 0.0, color, lineWidth, fillStyle);
            }
            return null;
        } else if (type.equalsIgnoreCase("rectangle")) {
            if (pts != null && pts.length >= 1 && pts[0] != null) {
                Point start = pts[0];
                return Rectangle.CreateRectangle(pts[0].getX(), pts[1].getX(), pts[0].getY(), pts[1].getY(), color, lineWidth, fillStyle);
            }
            return null;
        } else if (type.equalsIgnoreCase("square")) {
            if (pts != null && pts.length >= 1 && pts[0] != null) {
                Point start = pts[0];
                return Square.CreateSquare(pts[0].getX(), pts[1].getX(), pts[0].getY(), pts[1].getY(), color, lineWidth, fillStyle);
            }
            return null;
        } else if (type.equalsIgnoreCase("oval")) {
            if (pts != null && pts.length >= 1 && pts[0] != null) {
                Point start = pts[0];
                return Oval.createOval(pts[0].getX(), pts[1].getX(), pts[0].getY(), pts[1].getY(), color, lineWidth, fillStyle);
            }
            return null;
        } else if (type.equalsIgnoreCase("triangle")) {
            return new Triangle(pts[0], pts[1], pts[2], color, lineWidth, fillStyle);
        } else if (type.equalsIgnoreCase("polyline")) {
            return new Polyline(color, lineWidth);
        } else if (type.equalsIgnoreCase("squiggle")) {
            return new Squiggle(color, lineWidth);
        } else {
            return null;
        }
    }
}
