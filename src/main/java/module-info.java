module com.paintapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;


    exports com.paintapp.scribble;
    exports com.paintapp.paint;
    exports com.paintapp.paint.shapes;
    exports com.paintapp.paint.strategy;
    exports com.paintapp.paint.command.pattern;
    exports com.paintapp.paint.app;
    exports com.paintapp.paint.persistence;


}