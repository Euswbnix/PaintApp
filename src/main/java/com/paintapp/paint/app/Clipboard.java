package com.paintapp.paint.app;

import com.paintapp.paint.shapes.Drawable;

import java.util.ArrayList;
import java.util.List;


/**
 * Representation of a clipboard which stores copies of drawable shape objects
 * that can be pasted onto the screen.
 * Implements the singleton design pattern such that there exists just one clipboard
 * where we can access that clipboard with the getInstance method.
 * While allowing mechanism of the clipboard such as copying and or pasting objects.
 */
public class Clipboard {
    /**
     * Static Clipboard object that ensures the program has one clipboard.
     */
    private static Clipboard instance;
    /**
     * ArrayList keeping track of the contents in our clipboard
     */
    private List<Drawable> contents = new ArrayList<>();
    /**
     * int Object keeping track of the number of objects pasted
     */
    private int pastedCount = 0;

    /**
     * RETURNS itself, the current clipboard.
     * @return the current clipboard  object.
     */
    public static Clipboard getInstance() {
        if (instance == null) {
            instance = new Clipboard();
        }
        return instance;
    }

    /**
     * Adds the drawable shapes in the shapes ArrayList to the connects Arraylist.
     * This acts as the storage mechanism for the clipboard.
     *
     * @param shapes
     */
    public void copy(List<Drawable> shapes) {
        contents.clear();
        for (Drawable s : shapes) {
            contents.add(s.clone());
        }
        pastedCount = 0;
    }

    /**
     * RETURNS a clone of the shapes in the contents of the clipboard offset.
     * @return the Arraylist of all the pasted shapes
     */
    public List<Drawable> paste() {
        List<Drawable> pasted = new ArrayList<>();
        pastedCount++;
        for (Drawable s : contents) {
            Drawable d = s.clone();
            d.offset(20 * pastedCount, 20 * pastedCount);
            pasted.add(d);
        }
        return pasted;
    }

    /**
     * RETURNS true if the clipboard contents are empty, otherwise false.
     * @return true if the contents ArrayList is empty, otherwise false.
     */
    public boolean isEmpty() {
        return contents.isEmpty();
    }
}
