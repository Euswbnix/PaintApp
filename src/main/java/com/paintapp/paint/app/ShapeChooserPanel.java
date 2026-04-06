package com.paintapp.paint.app;

import com.paintapp.paint.strategy.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Represents shape chooser panel on the paint panel.
 * This Creates the panel and wires it to the main model-view architecture
 */
public class ShapeChooserPanel extends GridPane implements EventHandler<ActionEvent> {
    /**
     * View object that handles the presentation for the ShapeChooserPanel
     */
    private View view;
    /**
     * Button object that represents the selected button
     */
    private Button selectedButton;

    /**
     * Contracts a new ShapeChooserPanel object with where buttons, picker, and toggle
     * are built and handlers are registered to update the buttons, picker, and toggle.
     * @param view
     */
    public ShapeChooserPanel(View view) {
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setTooltip(new Tooltip("Select drawing color"));
        colorPicker.setOnAction(event -> {
            Color newcolor = colorPicker.getValue();
            view.setCurrentColor(newcolor);
            view.getPaintModel().updateSelectedShapesColor(newcolor);
        });
        this.add(colorPicker, 1, 0);
        Slider lineWidthSlider = new Slider(1, 20, 1);
        lineWidthSlider.setShowTickMarks(true);
        lineWidthSlider.setShowTickLabels(true);
        lineWidthSlider.setMajorTickUnit(5);
        lineWidthSlider.setMinorTickCount(4);
        lineWidthSlider.setBlockIncrement(1);
        lineWidthSlider.setTooltip(new Tooltip("Select line width"));
        Text label = new Text("Line Width: ");
        this.add(label, 1, 1);
        this.add(lineWidthSlider, 1, 2);
        this.view = view;
        lineWidthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            view.setCurrentLineWidth(newWidth);
            view.getPaintModel().updateSelectedShapesLineWidth(newWidth);
        });
        Button fillStyleButton = new Button("Fill Style (default: Filled)");
        fillStyleButton.setTooltip(new Tooltip("Select fill style (default: Filled)"));
        fillStyleButton.setMinWidth(100);
        fillStyleButton.setOnAction(event -> {
            FillStyle newStyle;
            if (view.getCurrentFillStyle() == FillStyle.FILLED) {
                newStyle = FillStyle.OUTLINE;
                view.setCurrentFillStyle(newStyle);
                fillStyleButton.setText("Fill: Outline");
            } else {
                newStyle = FillStyle.FILLED;
                view.setCurrentFillStyle(newStyle);
                fillStyleButton.setText("Fill: Filled");
            }
            view.getPaintModel().updateSelectedShapesFillStyle(newStyle);
        });
        this.add(fillStyleButton, 1, 3);

        String[] buttonLabels = {"Select", "Circle", "Rectangle", "Square", "Triangle", "Oval", "Squiggle", "Polyline"};

        int row = 0;
        for (String labelText : buttonLabels) {
            String iconPath = "/icons/" + labelText.toLowerCase() + ".png";
            ImageView iconView = null;
            try {
                Image icon = new Image(getClass().getResourceAsStream(iconPath));
                iconView = new ImageView(icon);
                iconView.setFitWidth(24);
                iconView.setFitHeight(24);
            } catch (Exception e) {
                System.out.println("Could not load icon: " + labelText);
            }
            Button button = new Button(labelText, iconView);
            button.setMinWidth(100);
            button.setOnAction(this);
            this.add(button, 0, row);
            row++;
        }
    }

    /**
     * Handles the shape selection process.
     * Updating view's drawing strategy based on the selected shape.
     * While printing the selected command type to the console.
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        Button button = (Button) event.getSource();
        String command = button.getText();
        if (selectedButton != null) {
            selectedButton.setStyle("");
        }
        button.setStyle("-fx-background-color: lightblue;");
        selectedButton = button;
        switch (command) {
            case "Select" -> view.setDrawingStrategy(new SelectionStrategy());
            case "Circle" -> view.setDrawingStrategy(new CircleStrategy());
            case "Rectangle" -> view.setDrawingStrategy(new RectangleStrategy());
            case "Squiggle" -> view.setDrawingStrategy(new SquiggleStrategy());
            case "Square" -> view.setDrawingStrategy(new SquareStrategy());
            case "Triangle" -> view.setDrawingStrategy(new TriangleStrategy());
            case "Oval" -> view.setDrawingStrategy(new OvalStrategy());
            case "Polyline" -> view.setDrawingStrategy(new PolylineStrategy());
            default -> System.out.println("Unknown strategy: " + command);
        }
        System.out.println("Selected: " + command);

    }
}

