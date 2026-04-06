package com.paintapp.paint.persistence;

import com.paintapp.paint.app.FillStyle;
import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Circle;
import com.paintapp.paint.shapes.Drawable;
import com.paintapp.paint.shapes.Oval;
import com.paintapp.paint.shapes.Point;
import com.paintapp.paint.shapes.Polyline;
import com.paintapp.paint.shapes.Rectangle;
import com.paintapp.paint.shapes.Square;
import com.paintapp.paint.shapes.Squiggle;
import com.paintapp.paint.shapes.Triangle;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a Paint Save File (text format) into a {@link PaintModel}.
 *
 * The format is whitespace-insensitive: lines are normalized by removing
 * all whitespace before pattern matching, so the same file with or without
 * tabs / extra spaces parses identically.
 *
 * Supported shape blocks:
 *   Circle, Rectangle, Squiggle, Polyline       (Assignment 3 compatible)
 *   Oval, Square, Triangle                      (PaintApp extensions)
 *
 * Image shapes are not serialized in this format and are ignored on parse.
 *
 * On parse failure {@link #getErrorMessage()} returns a message of the
 * form {@code "Error in line N <details>"}, matching the convention used
 * by the Assignment 3 parser test suite.
 */
public class PaintFileParser {

    private int lineNumber = 0;
    private String errorMessage = "";
    private PaintModel paintModel;
    private boolean isParsed = false;

    // ---- Patterns -------------------------------------------------------

    private static final Pattern P_FILE_START = Pattern.compile("^PaintSaveFileVersion1\\.0$");
    private static final Pattern P_FILE_END   = Pattern.compile("^EndPaintSaveFile$");

    private static final Pattern P_CIRCLE_START   = Pattern.compile("^Circle$");
    private static final Pattern P_CIRCLE_END     = Pattern.compile("^EndCircle$");
    private static final Pattern P_RECT_START     = Pattern.compile("^Rectangle$");
    private static final Pattern P_RECT_END       = Pattern.compile("^EndRectangle$");
    private static final Pattern P_SQUIG_START    = Pattern.compile("^Squiggle$");
    private static final Pattern P_SQUIG_END      = Pattern.compile("^EndSquiggle$");
    private static final Pattern P_POLY_START     = Pattern.compile("^Polyline$");
    private static final Pattern P_POLY_END       = Pattern.compile("^EndPolyline$");
    private static final Pattern P_OVAL_START     = Pattern.compile("^Oval$");
    private static final Pattern P_OVAL_END       = Pattern.compile("^EndOval$");
    private static final Pattern P_SQUARE_START   = Pattern.compile("^Square$");
    private static final Pattern P_SQUARE_END     = Pattern.compile("^EndSquare$");
    private static final Pattern P_TRI_START      = Pattern.compile("^Triangle$");
    private static final Pattern P_TRI_END        = Pattern.compile("^EndTriangle$");

    private static final Pattern P_COLOR  = Pattern.compile("^color:(\\d+),(\\d+),(\\d+)$");
    private static final Pattern P_FILLED = Pattern.compile("^filled:(true|false)$");
    private static final Pattern P_CENTER = Pattern.compile("^center:\\((-?\\d+),(-?\\d+)\\)$");
    private static final Pattern P_RADIUS = Pattern.compile("^radius:(\\d+)$");
    private static final Pattern P_P1     = Pattern.compile("^p1:\\((-?\\d+),(-?\\d+)\\)$");
    private static final Pattern P_P2     = Pattern.compile("^p2:\\((-?\\d+),(-?\\d+)\\)$");
    private static final Pattern P_P3     = Pattern.compile("^p3:\\((-?\\d+),(-?\\d+)\\)$");
    private static final Pattern P_SIZE   = Pattern.compile("^size:(\\d+)$");
    private static final Pattern P_POINTS_OPEN  = Pattern.compile("^points$");
    private static final Pattern P_POINTS_CLOSE = Pattern.compile("^endpoints$");
    private static final Pattern P_POINT  = Pattern.compile("^point:\\((-?\\d+),(-?\\d+)\\)$");

    // ---- States ---------------------------------------------------------

    private static final int ST_HEADER       = 0;
    private static final int ST_SHAPE_OR_END = 1;
    private static final int ST_FILE_END     = 2;

    // Circle
    private static final int ST_CIRCLE_COLOR  = 10;
    private static final int ST_CIRCLE_FILLED = 11;
    private static final int ST_CIRCLE_CENTER = 12;
    private static final int ST_CIRCLE_RADIUS = 13;
    private static final int ST_CIRCLE_END    = 14;
    // Rectangle
    private static final int ST_RECT_COLOR  = 20;
    private static final int ST_RECT_FILLED = 21;
    private static final int ST_RECT_P1     = 22;
    private static final int ST_RECT_P2     = 23;
    private static final int ST_RECT_END    = 24;
    // Squiggle
    private static final int ST_SQUIG_COLOR        = 30;
    private static final int ST_SQUIG_FILLED       = 31;
    private static final int ST_SQUIG_POINTS_OPEN  = 32;
    private static final int ST_SQUIG_POINT_OR_END = 33;
    private static final int ST_SQUIG_END          = 34;
    // Polyline
    private static final int ST_POLY_COLOR        = 40;
    private static final int ST_POLY_FILLED       = 41;
    private static final int ST_POLY_POINTS_OPEN  = 42;
    private static final int ST_POLY_POINT_OR_END = 43;
    private static final int ST_POLY_END          = 44;
    // Oval (extension)
    private static final int ST_OVAL_COLOR  = 50;
    private static final int ST_OVAL_FILLED = 51;
    private static final int ST_OVAL_P1     = 52;
    private static final int ST_OVAL_P2     = 53;
    private static final int ST_OVAL_END    = 54;
    // Square (extension)
    private static final int ST_SQUARE_COLOR  = 60;
    private static final int ST_SQUARE_FILLED = 61;
    private static final int ST_SQUARE_P1     = 62;
    private static final int ST_SQUARE_SIZE   = 63;
    private static final int ST_SQUARE_END    = 64;
    // Triangle (extension)
    private static final int ST_TRI_COLOR  = 70;
    private static final int ST_TRI_FILLED = 71;
    private static final int ST_TRI_P1     = 72;
    private static final int ST_TRI_P2     = 73;
    private static final int ST_TRI_P3     = 74;
    private static final int ST_TRI_END    = 75;

    // ---- Public API -----------------------------------------------------

    /** @return the error message from the most recent unsuccessful parse */
    public String getErrorMessage() {
        return errorMessage;
    }

    /** @return the populated {@link PaintModel} after a successful parse */
    public PaintModel getPaintModel() {
        return paintModel;
    }

    /** @return whether the most recent parse succeeded */
    public boolean isParsed() {
        return isParsed;
    }

    /**
     * Parse the file at {@code fileName}.
     * @return whether the file was parsed successfully
     */
    public boolean parse(String fileName) {
        boolean result = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            result = parse(br, new PaintModel());
        } catch (FileNotFoundException e) {
            error("File Not Found: " + fileName);
        } finally {
            try { if (br != null) br.close(); } catch (Exception ignored) { }
        }
        isParsed = result;
        return result;
    }

    /**
     * Parse the contents of {@code in} into {@code model}.
     * Public so unit tests can drive the parser directly.
     */
    public boolean parse(BufferedReader in, PaintModel model) {
        this.paintModel = model;
        this.errorMessage = "";
        this.lineNumber = 0;

        // Per-shape scratch buffers populated as we walk through the input.
        Color color = null;
        boolean filled = false;
        Point p1 = null, p2 = null, p3 = null;
        int radius = 0;
        int sizeVal = 0;
        List<Point> pointBuffer = new ArrayList<>();

        int state = ST_HEADER;
        try {
            String raw;
            while ((raw = in.readLine()) != null) {
                lineNumber++;
                if (raw.trim().isEmpty()) continue;
                String line = raw.replaceAll("\\s+", "");

                switch (state) {
                    case ST_HEADER:
                        if (P_FILE_START.matcher(line).matches()) {
                            state = ST_SHAPE_OR_END;
                        } else {
                            error("Expected Start of Paint Save File");
                            return false;
                        }
                        break;

                    case ST_SHAPE_OR_END:
                        if (P_CIRCLE_START.matcher(line).matches())     { state = ST_CIRCLE_COLOR; }
                        else if (P_RECT_START.matcher(line).matches())  { state = ST_RECT_COLOR; }
                        else if (P_SQUIG_START.matcher(line).matches()) { state = ST_SQUIG_COLOR;  pointBuffer.clear(); }
                        else if (P_POLY_START.matcher(line).matches())  { state = ST_POLY_COLOR;   pointBuffer.clear(); }
                        else if (P_OVAL_START.matcher(line).matches())  { state = ST_OVAL_COLOR; }
                        else if (P_SQUARE_START.matcher(line).matches()){ state = ST_SQUARE_COLOR; }
                        else if (P_TRI_START.matcher(line).matches())   { state = ST_TRI_COLOR; }
                        else if (P_FILE_END.matcher(line).matches())    { state = ST_FILE_END; }
                        else {
                            error("Expected Start of Shape or End Paint Save File");
                            return false;
                        }
                        break;

                    case ST_FILE_END:
                        error("Extra content after End of File");
                        return false;

                    // -------- Circle --------
                    case ST_CIRCLE_COLOR:
                        color = parseColor(line, "Circle");
                        if (color == null) return false;
                        state = ST_CIRCLE_FILLED;
                        break;
                    case ST_CIRCLE_FILLED: {
                        Boolean f = parseFilled(line, "Circle");
                        if (f == null) return false;
                        filled = f;
                        state = ST_CIRCLE_CENTER;
                        break;
                    }
                    case ST_CIRCLE_CENTER: {
                        Matcher m = P_CENTER.matcher(line);
                        if (!m.matches()) { error("Expected Circle center"); return false; }
                        p1 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_CIRCLE_RADIUS;
                        break;
                    }
                    case ST_CIRCLE_RADIUS: {
                        Matcher m = P_RADIUS.matcher(line);
                        if (!m.matches()) { error("Expected Circle Radius"); return false; }
                        radius = Integer.parseInt(m.group(1));
                        if (radius < 0) { error("Expected Circle Radius"); return false; }
                        state = ST_CIRCLE_END;
                        break;
                    }
                    case ST_CIRCLE_END:
                        if (!P_CIRCLE_END.matcher(line).matches()) {
                            error("Expected End Circle"); return false;
                        }
                        addShape(new Circle(p1, radius, color, 1.0,
                                filled ? FillStyle.FILLED : FillStyle.OUTLINE));
                        state = ST_SHAPE_OR_END;
                        break;

                    // -------- Rectangle --------
                    case ST_RECT_COLOR:
                        color = parseColor(line, "Rectangle");
                        if (color == null) return false;
                        state = ST_RECT_FILLED;
                        break;
                    case ST_RECT_FILLED: {
                        Boolean f = parseFilled(line, "Rectangle");
                        if (f == null) return false;
                        filled = f;
                        state = ST_RECT_P1;
                        break;
                    }
                    case ST_RECT_P1: {
                        Matcher m = P_P1.matcher(line);
                        if (!m.matches()) { error("Expected Rectangle p1"); return false; }
                        p1 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_RECT_P2;
                        break;
                    }
                    case ST_RECT_P2: {
                        Matcher m = P_P2.matcher(line);
                        if (!m.matches()) { error("Expected Rectangle p2"); return false; }
                        p2 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_RECT_END;
                        break;
                    }
                    case ST_RECT_END:
                        if (!P_RECT_END.matcher(line).matches()) {
                            error("Expected End Rectangle"); return false;
                        }
                        addShape(Rectangle.CreateRectangle(p1.x, p2.x, p1.y, p2.y, color, 1.0,
                                filled ? FillStyle.FILLED : FillStyle.OUTLINE));
                        state = ST_SHAPE_OR_END;
                        break;

                    // -------- Squiggle --------
                    case ST_SQUIG_COLOR:
                        color = parseColor(line, "Squiggle");
                        if (color == null) return false;
                        state = ST_SQUIG_FILLED;
                        break;
                    case ST_SQUIG_FILLED: {
                        Boolean f = parseFilled(line, "Squiggle");
                        if (f == null) return false;
                        filled = f;
                        state = ST_SQUIG_POINTS_OPEN;
                        break;
                    }
                    case ST_SQUIG_POINTS_OPEN:
                        if (!P_POINTS_OPEN.matcher(line).matches()) {
                            error("Expected Squiggle points"); return false;
                        }
                        pointBuffer.clear();
                        state = ST_SQUIG_POINT_OR_END;
                        break;
                    case ST_SQUIG_POINT_OR_END: {
                        Matcher m = P_POINT.matcher(line);
                        if (m.matches()) {
                            pointBuffer.add(new Point(Integer.parseInt(m.group(1)),
                                    Integer.parseInt(m.group(2))));
                            break;
                        }
                        if (P_POINTS_CLOSE.matcher(line).matches()) {
                            state = ST_SQUIG_END;
                            break;
                        }
                        error("Expected Squiggle point or end points");
                        return false;
                    }
                    case ST_SQUIG_END: {
                        if (!P_SQUIG_END.matcher(line).matches()) {
                            error("Expected End Squiggle"); return false;
                        }
                        Squiggle s = new Squiggle(color, 1.0);
                        for (Point pt : pointBuffer) s.addPoint(pt);
                        addShape(s);
                        pointBuffer.clear();
                        state = ST_SHAPE_OR_END;
                        break;
                    }

                    // -------- Polyline --------
                    case ST_POLY_COLOR:
                        color = parseColor(line, "Polyline");
                        if (color == null) return false;
                        state = ST_POLY_FILLED;
                        break;
                    case ST_POLY_FILLED: {
                        Boolean f = parseFilled(line, "Polyline");
                        if (f == null) return false;
                        filled = f;
                        state = ST_POLY_POINTS_OPEN;
                        break;
                    }
                    case ST_POLY_POINTS_OPEN:
                        if (!P_POINTS_OPEN.matcher(line).matches()) {
                            error("Expected Polyline points"); return false;
                        }
                        pointBuffer.clear();
                        state = ST_POLY_POINT_OR_END;
                        break;
                    case ST_POLY_POINT_OR_END: {
                        Matcher m = P_POINT.matcher(line);
                        if (m.matches()) {
                            pointBuffer.add(new Point(Integer.parseInt(m.group(1)),
                                    Integer.parseInt(m.group(2))));
                            break;
                        }
                        if (P_POINTS_CLOSE.matcher(line).matches()) {
                            state = ST_POLY_END;
                            break;
                        }
                        error("Expected Polyline point or end points");
                        return false;
                    }
                    case ST_POLY_END: {
                        if (!P_POLY_END.matcher(line).matches()) {
                            error("Expected End Polyline"); return false;
                        }
                        Polyline pl = new Polyline(color, 1.0);
                        for (Point pt : pointBuffer) pl.addPoint(pt);
                        addShape(pl);
                        pointBuffer.clear();
                        state = ST_SHAPE_OR_END;
                        break;
                    }

                    // -------- Oval (extension) --------
                    case ST_OVAL_COLOR:
                        color = parseColor(line, "Oval");
                        if (color == null) return false;
                        state = ST_OVAL_FILLED;
                        break;
                    case ST_OVAL_FILLED: {
                        Boolean f = parseFilled(line, "Oval");
                        if (f == null) return false;
                        filled = f;
                        state = ST_OVAL_P1;
                        break;
                    }
                    case ST_OVAL_P1: {
                        Matcher m = P_P1.matcher(line);
                        if (!m.matches()) { error("Expected Oval p1"); return false; }
                        p1 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_OVAL_P2;
                        break;
                    }
                    case ST_OVAL_P2: {
                        Matcher m = P_P2.matcher(line);
                        if (!m.matches()) { error("Expected Oval p2"); return false; }
                        p2 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_OVAL_END;
                        break;
                    }
                    case ST_OVAL_END:
                        if (!P_OVAL_END.matcher(line).matches()) {
                            error("Expected End Oval"); return false;
                        }
                        addShape(Oval.createOval(p1.x, p2.x, p1.y, p2.y, color, 1.0,
                                filled ? FillStyle.FILLED : FillStyle.OUTLINE));
                        state = ST_SHAPE_OR_END;
                        break;

                    // -------- Square (extension) --------
                    case ST_SQUARE_COLOR:
                        color = parseColor(line, "Square");
                        if (color == null) return false;
                        state = ST_SQUARE_FILLED;
                        break;
                    case ST_SQUARE_FILLED: {
                        Boolean f = parseFilled(line, "Square");
                        if (f == null) return false;
                        filled = f;
                        state = ST_SQUARE_P1;
                        break;
                    }
                    case ST_SQUARE_P1: {
                        Matcher m = P_P1.matcher(line);
                        if (!m.matches()) { error("Expected Square p1"); return false; }
                        p1 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_SQUARE_SIZE;
                        break;
                    }
                    case ST_SQUARE_SIZE: {
                        Matcher m = P_SIZE.matcher(line);
                        if (!m.matches()) { error("Expected Square size"); return false; }
                        sizeVal = Integer.parseInt(m.group(1));
                        if (sizeVal < 0) { error("Expected Square size"); return false; }
                        state = ST_SQUARE_END;
                        break;
                    }
                    case ST_SQUARE_END:
                        if (!P_SQUARE_END.matcher(line).matches()) {
                            error("Expected End Square"); return false;
                        }
                        addShape(new Square(p1.x, p1.y, sizeVal, color, 1.0,
                                filled ? FillStyle.FILLED : FillStyle.OUTLINE));
                        state = ST_SHAPE_OR_END;
                        break;

                    // -------- Triangle (extension) --------
                    case ST_TRI_COLOR:
                        color = parseColor(line, "Triangle");
                        if (color == null) return false;
                        state = ST_TRI_FILLED;
                        break;
                    case ST_TRI_FILLED: {
                        Boolean f = parseFilled(line, "Triangle");
                        if (f == null) return false;
                        filled = f;
                        state = ST_TRI_P1;
                        break;
                    }
                    case ST_TRI_P1: {
                        Matcher m = P_P1.matcher(line);
                        if (!m.matches()) { error("Expected Triangle p1"); return false; }
                        p1 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_TRI_P2;
                        break;
                    }
                    case ST_TRI_P2: {
                        Matcher m = P_P2.matcher(line);
                        if (!m.matches()) { error("Expected Triangle p2"); return false; }
                        p2 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_TRI_P3;
                        break;
                    }
                    case ST_TRI_P3: {
                        Matcher m = P_P3.matcher(line);
                        if (!m.matches()) { error("Expected Triangle p3"); return false; }
                        p3 = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                        state = ST_TRI_END;
                        break;
                    }
                    case ST_TRI_END:
                        if (!P_TRI_END.matcher(line).matches()) {
                            error("Expected End Triangle"); return false;
                        }
                        addShape(new Triangle(p1, p2, p3, color, 1.0,
                                filled ? FillStyle.FILLED : FillStyle.OUTLINE));
                        state = ST_SHAPE_OR_END;
                        break;

                    default:
                        error("Internal parser error");
                        return false;
                }
            }
        } catch (Exception e) {
            error("Exception during parsing: " + e.getMessage());
            return false;
        }

        if (state != ST_FILE_END) {
            error("Unexpected end of file");
            return false;
        }
        return true;
    }

    // ---- Helpers --------------------------------------------------------

    private void addShape(Drawable d) {
        paintModel.getDrawables().add(d);
    }

    private Color parseColor(String line, String shapeName) {
        Matcher m = P_COLOR.matcher(line);
        if (!m.matches()) {
            error("Expected " + shapeName + " color");
            return null;
        }
        int r = Integer.parseInt(m.group(1));
        int g = Integer.parseInt(m.group(2));
        int b = Integer.parseInt(m.group(3));
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            error("Expected " + shapeName + " color");
            return null;
        }
        return Color.rgb(r, g, b);
    }

    private Boolean parseFilled(String line, String shapeName) {
        Matcher m = P_FILLED.matcher(line);
        if (!m.matches()) {
            error("Expected " + shapeName + " filled");
            return null;
        }
        return Boolean.parseBoolean(m.group(1));
    }

    private void error(String message) {
        this.errorMessage = "Error in line " + lineNumber + " " + message;
    }
}
