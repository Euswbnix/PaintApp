package com.paintapp.scribble;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ScribblePanel extends Canvas implements EventHandler<MouseEvent> {
    public ScribblePanel() {
        super(200, 200);
        this.addEventHandler(MouseEvent.MOUSE_MOVED, this);
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        GraphicsContext gc = this.getGraphicsContext2D();
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        gc.setFill(Color.RED);
        gc.fillRect(x, y, 1, 1);
    }
}
