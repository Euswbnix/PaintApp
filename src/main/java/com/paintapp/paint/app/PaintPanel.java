package com.paintapp.paint.app;

import com.paintapp.paint.strategy.DrawingStrategy;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Observable;
import java.util.Observer;

/**
 * The PaintPanel objects represents the paint canvas for the application which implements
 * the EventHandler<MouseEvent> and Observer interface.
 * Implementing EventHandler<MouseEvent> allows us to access the interactions from the
 * user's mouse.
 * While Observer interface allows the PaintPanel to observer the PaintModel to get notifying
 * when the data in the PaintModel gets updated.
 */
public class PaintPanel extends Canvas implements EventHandler<MouseEvent>, Observer {
    /**
     * PaintModel object that contains the data for our PaintPanel
     */
    private PaintModel model;
    /**
     * Color object represents the current color, the default is black.
     */
    private Color currentColor = Color.BLACK;
    /**
     * DrawingStrategy object represents the current drawing strategy for the
     * shape.
     */
    private DrawingStrategy currentStrategy;
    /**
     * double object that represents the current line width of the drawable shapes.
     * The default line width is 1.
     */
    private double currentLineWidth = 1;
    /**
     * FillStyle object that represents the current fill style of the drawable shapes.
     * The default fill style is filled.
     */
    private FillStyle currentFillStyle = FillStyle.FILLED;

    /**
     * Change the color of the paint panel shapes to the color given.
     * @param color
     */
    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    /**
     * Changes the line width of the paint panel shapes to the given line width, width.
     * @param width
     */
    public void setCurrentLineWidth(double width) {
        this.currentLineWidth = width;
    }

    /**
     * Constructs a new PaintPanel object with the data from the given PaintModel.
     * While adding the PaintPanel as an observer to the given PaintModel.
     * @param model
     */
    public PaintPanel(PaintModel model) {
        this.model = model;
        this.model.addObserver(this);
        this.widthProperty().addListener(evt -> update(model, null));
        this.heightProperty().addListener(evt -> update(model, null));
        this.addEventHandler(MouseEvent.ANY, this);
        this.setFocusTraversable(true);
        this.setOnKeyPressed(this::handleKeyPress);
        this.setOnMouseEntered(e -> this.requestFocus());
    }

    /**
     * If the escape key is pressed the selected shapes are unselected.
     * If the delete key is pressed the selected shapes are deleted.
     * @param e keyEvent to get the key codes.
     */
    public void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                model.clearSelection();
                System.out.println("Selection cleared with ESC key");
                break;
            case DELETE:
                model.delete();
                System.out.println("Deleted with DELETE key");
                break;
        }
    }

    /**
     * Changes the drawing strategy to the given DrawingStrategy object, strategy
     * @param strategy
     */
    public void setDrawingStrategy(DrawingStrategy strategy) {
        this.currentStrategy = strategy;
    }

    /**
     * Changes the fill style to the given FillStyle, style.
     * @param style
     */
    public void setFillStyle(FillStyle style) {
        this.currentFillStyle = style;
    }

    /**
     * Runs the drawing strategy for the specific drawable shape, which handles the drawing process
     * when the shape is being drawn.
     * @param mouseEvent
     */
    @Override
    public void handle(MouseEvent mouseEvent) {
        if (this.currentStrategy != null) {
            currentStrategy.handle(mouseEvent, model, currentColor, currentLineWidth, currentFillStyle);
        }
    }

    /**
     * Updates the shapes drawings in the paint panel.
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers} method.
     */
    @Override
    public void update(Observable o, Object arg) {
        GraphicsContext g2d = this.getGraphicsContext2D();
        g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
        model.draw(g2d);
    }
}