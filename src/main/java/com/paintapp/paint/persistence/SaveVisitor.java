package com.paintapp.paint.persistence;

import com.paintapp.paint.shapes.Circle;
import com.paintapp.paint.shapes.ImageS;
import com.paintapp.paint.shapes.Oval;
import com.paintapp.paint.shapes.Polyline;
import com.paintapp.paint.shapes.Rectangle;
import com.paintapp.paint.shapes.Square;
import com.paintapp.paint.shapes.Squiggle;
import com.paintapp.paint.shapes.Triangle;

/**
 * Visitor for serializing shapes. Lets us add new save formats
 * (or other operations) without modifying the shape classes themselves.
 *
 * Each Drawable implements {@code accept(SaveVisitor v)} which simply
 * dispatches to the appropriate {@code visit(...)} overload.
 */
public interface SaveVisitor {
    void visit(Circle c);
    void visit(Rectangle r);
    void visit(Squiggle s);
    void visit(Polyline p);
    void visit(Oval o);
    void visit(Square s);
    void visit(Triangle t);
    void visit(ImageS i);
}
