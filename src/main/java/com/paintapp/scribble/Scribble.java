package com.paintapp.scribble;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Scribble extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /**
         * A stage is the top level GUI window.
         * A stage has a scene.
         * A scene is a tree/graph of stuff, that is. Nodes in the scene are ...
         *
         * LAYOUTS: which organize how its subtrees appear
         * https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm#CHDGHCDG
         *
         * CONTROLS: things which you interact with...
         * https://docs.oracle.com/javafx/2/ui_controls/jfxpub-ui_controls.htm
         *
         * EVENTS: Controls communicate with your code through callbacks. Basically the Observable pattern.
         *
         **/

        //  Canvas canvas = new Canvas(200,200);
        ScribblePanel scribblePanel = new ScribblePanel();
        HBox root = new HBox(); // LAYOUT
        root.setPadding(new Insets(5));
        root.getChildren().add(scribblePanel);

        Scene scene = new Scene(root); // SCENE

        stage.setTitle("Scribble");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
        /**
         * static method of Application
         * Creates an instance of Application,
         * starts the gui thread and calls
         * Application.start(stage) where stage is the window
         **/
    }
}
