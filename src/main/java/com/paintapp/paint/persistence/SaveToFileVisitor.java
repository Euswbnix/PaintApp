package com.paintapp.paint.persistence;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.shapes.Circle;
import com.paintapp.paint.shapes.ImageS;
import com.paintapp.paint.shapes.Oval;
import com.paintapp.paint.shapes.Point;
import com.paintapp.paint.shapes.Polyline;
import com.paintapp.paint.shapes.Rectangle;
import com.paintapp.paint.shapes.Square;
import com.paintapp.paint.shapes.Squiggle;
import com.paintapp.paint.shapes.Triangle;
import javafx.scene.paint.Color;

/**
 * Concrete SaveVisitor that serializes shapes to the
 * Paint Save File text format.
 *
 * - Circle / Rectangle / Squiggle / Polyline blocks use the same
 *   format as the Assignment 3 "PaintSaveFileVersion1.0" so files
 *   produced by this app are forwards/backwards compatible with that
 *   format for those four shape types.
 * - Oval / Square / Triangle / Image are PaintApp extensions and
 *   appear as additional blocks defined by this app.
 *
 * Line widths and exact double precision are intentionally not
 * serialized for the four legacy shapes (the format uses integers).
 * Reloaded shapes default to lineWidth = 1.0.
 */
public class SaveToFileVisitor implements SaveVisitor {

    private final StringBuilder data = new StringBuilder();

    /** @return the accumulated file contents */
    public String getData() {
        return data.toString();
    }

    @Override
    public void visit(Circle c) {
        data.append("Circle\n");
        data.append("\tcolor:").append(colorToString(c.getColor())).append("\n");
        data.append("\tfilled:").append(c.getFillStyle() == FillStyle.FILLED).append("\n");
        data.append("\tcenter:(").append((int) c.getCentre().x).append(",")
                .append((int) c.getCentre().y).append(")\n");
        data.append("\tradius:").append((int) c.getRadius()).append("\n");
        data.append("End Circle\n");
    }

    @Override
    public void visit(Rectangle r) {
        data.append("Rectangle\n");
        data.append("\tcolor:").append(colorToString(r.getColor())).append("\n");
        data.append("\tfilled:").append(r.getFillStyle() == FillStyle.FILLED).append("\n");
        data.append("\tp1:(").append((int) r.getX()).append(",")
                .append((int) r.getY()).append(")\n");
        data.append("\tp2:(").append((int) (r.getX() + r.getWidth())).append(",")
                .append((int) (r.getY() + r.getHeight())).append(")\n");
        data.append("End Rectangle\n");
    }

    @Override
    public void visit(Squiggle s) {
        data.append("Squiggle\n");
        data.append("\tcolor:").append(colorToString(s.getColor())).append("\n");
        data.append("\tfilled:false\n");
        data.append("\tpoints\n");
        for (Point p : s.getPoints()) {
            data.append("\t\tpoint:(").append((int) p.x).append(",")
                    .append((int) p.y).append(")\n");
        }
        data.append("\tend points\n");
        data.append("End Squiggle\n");
    }

    @Override
    public void visit(Polyline p) {
        data.append("Polyline\n");
        data.append("\tcolor:").append(colorToString(p.getColor())).append("\n");
        data.append("\tfilled:false\n");
        data.append("\tpoints\n");
        for (Point pt : p.getPoints()) {
            data.append("\t\tpoint:(").append((int) pt.x).append(",")
                    .append((int) pt.y).append(")\n");
        }
        data.append("\tend points\n");
        data.append("End Polyline\n");
    }

    @Override
    public void visit(Oval o) {
        data.append("Oval\n");
        data.append("\tcolor:").append(colorToString(o.getColor())).append("\n");
        data.append("\tfilled:").append(o.getFillStyle() == FillStyle.FILLED).append("\n");
        data.append("\tp1:(").append((int) o.getX()).append(",")
                .append((int) o.getY()).append(")\n");
        data.append("\tp2:(").append((int) (o.getX() + o.getWidth())).append(",")
                .append((int) (o.getY() + o.getHeight())).append(")\n");
        data.append("End Oval\n");
    }

    @Override
    public void visit(Square s) {
        data.append("Square\n");
        data.append("\tcolor:").append(colorToString(s.getColor())).append("\n");
        data.append("\tfilled:").append(s.getFillStyle() == FillStyle.FILLED).append("\n");
        data.append("\tp1:(").append((int) s.getX()).append(",")
                .append((int) s.getY()).append(")\n");
        data.append("\tsize:").append((int) s.getSize()).append("\n");
        data.append("End Square\n");
    }

    @Override
    public void visit(Triangle t) {
        data.append("Triangle\n");
        data.append("\tcolor:").append(colorToString(t.getColor())).append("\n");
        data.append("\tfilled:").append(t.getFillStyle() == FillStyle.FILLED).append("\n");
        data.append("\tp1:(").append((int) t.getA().x).append(",")
                .append((int) t.getA().y).append(")\n");
        data.append("\tp2:(").append((int) t.getB().x).append(",")
                .append((int) t.getB().y).append(")\n");
        data.append("\tp3:(").append((int) t.getC().x).append(",")
                .append((int) t.getC().y).append(")\n");
        data.append("End Triangle\n");
    }

    @Override
    public void visit(ImageS i) {
        // Imported images are an in-app feature only and are intentionally
        // not serialized to the Paint Save File format. Skipping them keeps
        // saved files compatible with the parser, which has no Image block.
    }

    private static String colorToString(Color color) {
        if (color == null) return "0,0,0";
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return r + "," + g + "," + b;
    }
}
