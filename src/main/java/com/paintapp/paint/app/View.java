package com.paintapp.paint.app;

import com.paintapp.paint.persistence.PaintFileParser;
import com.paintapp.paint.persistence.PaintFileSaver;
import com.paintapp.paint.shapes.ImageS;
import com.paintapp.paint.strategy.DrawingStrategy;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.File;

/**
 * Represents the presentation of the application in which we create the JavaFX
 * scene graph for the paint program as the class represents the view portion for the
 * MVC architecture.
 */
public class View implements EventHandler<ActionEvent> {
    /**
     * PaintModel object representing the data of the paint panel.
     */
    private PaintModel paintModel;
    /**
     * PaintPanel object that observes the paint model.
     */
    private PaintPanel paintPanel;
    /**
     * ShapeChooserPanel object that represents the shape choosing mechanism for
     * the application.
     */
    private ShapeChooserPanel shapeChooserPanel;
    /**
     * Color object represents the color of the shapes with the initial color
     * being black.
     */
    private Color currentColor = Color.BLACK;
    /**
     * double object that represents the current line width of the drawable shapes.
     * The default line width is 1.
     */
    private double currentLineWidth = 1.0;
    /**
     * FillStyle object that represents the current fill style of the drawable shapes.
     * The default fill style is filled.
     */
    private FillStyle currentFillStyle = FillStyle.FILLED;
    /**
     * Stage object that contains the JavaFX content
     */
    private Stage stage;

    /**
     * Contracts a View object with the given paint model and stage
     * @param model
     * @param stage
     */
    public View(PaintModel model, Stage stage) {
        this.paintModel = model;

        this.paintPanel = new PaintPanel(this.paintModel);
        this.shapeChooserPanel = new ShapeChooserPanel(this);
        this.paintPanel.setCurrentLineWidth(this.currentLineWidth);
        this.paintPanel.setCurrentColor(this.currentColor);

        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        StackPane canvasContainer = new StackPane(this.paintPanel);
        root.setCenter(canvasContainer);
        this.paintPanel.widthProperty().bind(canvasContainer.widthProperty());
        this.paintPanel.heightProperty().bind(canvasContainer.heightProperty());
        root.setLeft(this.shapeChooserPanel);
        Text statusText = new Text("Ready");
        statusText.setStyle("-fx-padding: 5px;");
        root.setBottom(statusText);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Paint");
        stage.show();
    }

    /**
     * RETURNS the paint model for the View instance
     * @return the paintModel attribute
     */
    public PaintModel getPaintModel() {
        return this.paintModel;
    }

    /**
     * Changes the color of the paint panel.
     * @param color
     */
    public void setCurrentColor(Color color) {
        this.currentColor = color;
        this.paintPanel.setCurrentColor(color);
    }

    /**
     * Changes the line width of the paint panel to width.
     * @param width
     */
    public void setCurrentLineWidth(double width) {
        this.currentLineWidth = width;
        this.paintPanel.setCurrentLineWidth(width);
    }

    /**
     * Changes the fill style of the paint panel to style.
     * @param style
     */
    public void setCurrentFillStyle(FillStyle style) {
        this.currentFillStyle = style;
        this.paintPanel.setFillStyle(style);
    }

    /**
     * RETURNS the current fill style
     * @return currentFillStyle attribute
     */
    public FillStyle getCurrentFillStyle() {
        return this.currentFillStyle;
    }

    /**
     * Changes the paint panel's drawing strategy to the given DrawingStrategy
     * object strategy.
     * @param strategy
     */
    public void setDrawingStrategy(DrawingStrategy strategy) {
        this.paintPanel.setDrawingStrategy(strategy);
    }

    /**
     * Creates the top menu bar for the application then RETURNS it
     * @return the created menu bar
     */
    private MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();
        Menu menu;
        MenuItem menuItem;

        // A menu for File

        menu = new Menu("File");

        menuItem = new MenuItem("New");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Open");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Save");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menu.getItems().add(new SeparatorMenuItem());

        menuItem = new MenuItem("Import Image");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menu.getItems().add(new SeparatorMenuItem());

        menuItem = new MenuItem("Exit");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        // Another menu for Edit

        menu = new Menu("Edit");

        menuItem = new MenuItem("Cut");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Copy");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Paste");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menu.getItems().add(new SeparatorMenuItem());

        menuItem = new MenuItem("Delete");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menu.getItems().add(new SeparatorMenuItem());

        menuItem = new MenuItem("Undo");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Redo");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        return menuBar;
    }

    /**
     * Allows the user to import images
     */
    public void handleImportImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"),
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fc.showOpenDialog(stage);
        if (selectedFile != null) {
            try{
                Image i = new Image(selectedFile.toURI().toString());
                if (i.isError()) {
                    showErrorLog("Failed to load image",
                            "The selected file cannot be loded as an image.");
                    return;
                }
                double imgWidth = i.getWidth();
                double imgHeight = i.getHeight();
                double maxSize = 300;
                if (imgWidth > maxSize || imgHeight > maxSize) {
                    double scale = Math.min(maxSize / imgWidth, maxSize / imgHeight);
                    imgWidth *= scale;
                    imgHeight *= scale;
                }
                double x = (paintPanel.getWidth() - imgWidth) / 2;
                double y = (paintPanel.getHeight() - imgHeight) / 2;
                ImageS ishape = new ImageS(x, y, imgWidth, imgHeight, i, selectedFile.getAbsolutePath());
                paintModel.addDrawableWithCommand(ishape);
                System.out.println("Image Imported");
            } catch (Exception e) {
                showErrorLog("Error while importing image"
                        ,"An error occurred while importing the image:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates and shows the error log.
     * @param title
     * @param msg
     */
    private void showErrorLog(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }


    /**
     * Based on the command action selected. The command is executed, undone, or redid
     * and the type of command that was completed is printed to the console.
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        String command = ((MenuItem) event.getSource()).getText();
        switch (command) {
            case "Exit" -> Platform.exit();
            case "New" -> handleNew();
            case "Open" -> handleOpen();
            case "Save" -> handleSave();
            case "Undo" -> {
                paintModel.undo();
                System.out.println("Undid");
            }
            case "Redo" -> {
                paintModel.redo();
                System.out.println("Redid");
            }
            case "Cut" -> {
                paintModel.cut();
                System.out.println("Cut");
            }
            case "Copy" -> {
                paintModel.copy();
                System.out.println("Copied");
            }
            case "Paste" -> {
                paintModel.paste();
                System.out.println("Pasted");
            }
            case "Delete" -> {
                paintModel.delete();
                System.out.println("Deleted");
            }
            case "Import Image" -> handleImportImage();
            default -> {
            }
        }
    }

    /**
     * Wipes the current canvas and starts a new empty document.
     */
    private void handleNew() {
        paintModel.clearAll();
        System.out.println("New canvas");
    }

    /**
     * Prompts the user for a Paint Save File and loads its shapes
     * into the current model.
     */
    private void handleOpen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Paint File");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Paint Save Files", "*.txt", "*.paint"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fc.showOpenDialog(stage);
        if (file == null) {
            System.out.println("Open cancelled");
            return;
        }
        PaintFileParser parser = new PaintFileParser();
        if (parser.parse(file.getAbsolutePath())) {
            paintModel.replaceWith(parser.getPaintModel());
            System.out.println("Opened " + file.getName());
        } else {
            showErrorLog("Failed to open file",
                    file.getName() + "\n" + parser.getErrorMessage());
        }
    }

    /**
     * Prompts the user for a destination and writes the current model
     * out in the Paint Save File format.
     */
    private void handleSave() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Paint File");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Paint Save Files", "*.txt", "*.paint"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fc.showSaveDialog(stage);
        if (file == null) {
            System.out.println("Save cancelled");
            return;
        }
        if (PaintFileSaver.save(paintModel, file)) {
            System.out.println("Saved to " + file.getAbsolutePath());
        } else {
            showErrorLog("Failed to save file",
                    "Could not write to " + file.getAbsolutePath());
        }
    }
}
