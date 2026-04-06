package com.paintapp.paint.persistence;

import com.paintapp.paint.app.PaintModel;
import com.paintapp.paint.shapes.Drawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Writes a PaintModel to a file in the Paint Save File format.
 */
public final class PaintFileSaver {

    private PaintFileSaver() {
        // utility class
    }

    /**
     * Serialize the given paint model to {@code file}.
     *
     * @param model the model to save
     * @param file  the destination file
     * @return whether the file was written successfully
     */
    public static boolean save(PaintModel model, File file) {
        SaveToFileVisitor visitor = new SaveToFileVisitor();
        for (Drawable d : model.getDrawables()) {
            d.accept(visitor);
        }
        try (PrintWriter w = new PrintWriter(file)) {
            w.println("Paint Save File Version 1.0");
            w.print(visitor.getData());
            w.println("End Paint Save File");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
