# PaintApp

A desktop drawing application built with **Java 22** and **JavaFX 22**. PaintApp lets you sketch with a variety of shapes and freehand tools, customize colors, line widths and fill styles, manipulate selections, import images, and undo/redo your work — all from a clean MVC-based JavaFX UI.

## Features

- **Drawing tools**
  - Circle, Oval, Square, Rectangle, Triangle
  - Polyline (multi-segment line)
  - Squiggle (freehand)
- **Style controls**
  - Stroke color picker
  - Adjustable line width
  - Filled / outlined fill styles
- **Selection & editing**
  - Select shapes on the canvas
  - Move selected shapes
  - Cut / Copy / Paste / Delete
- **History**
  - Unlimited Undo and Redo, powered by the Command pattern
- **Image import**
  - Load PNG / JPG / JPEG / BMP / GIF files onto the canvas as a movable image shape
- **Save / Open / New**
  - Save the current canvas to a human-readable text file (`*.txt` / `*.paint`)
  - Open a previously saved file and continue editing
  - Start a fresh canvas at any time via **File → New**
  - File format is the `Paint Save File Version 1.0` text format and supports all eight shape types

## Architecture

PaintApp follows a classic **Model–View–Controller** layout and showcases several Gang-of-Four design patterns:

| Layer / Pattern | Package | Purpose |
|---|---|---|
| Model | `com.paintapp.paint.app` (`PaintModel`, `Clipboard`, `FillStyle`) | Holds the document state — list of shapes, clipboard, current style settings |
| View + Controller | `com.paintapp.paint.app` (`View`, `PaintPanel`, `ShapeChooserPanel`) | Builds the JavaFX scene graph and routes user input to the model |
| Shapes | `com.paintapp.paint.shapes` | `Drawable` interface with concrete shapes (`Circle`, `Oval`, `Rectangle`, `Square`, `Triangle`, `Polyline`, `Squiggle`, `ImageS`) |
| **Factory pattern** | `ShapeFactory` | Centralized creation of `Drawable` instances |
| **Strategy pattern** | `com.paintapp.paint.strategy` | Each drawing tool (`CircleStrategy`, `SquiggleStrategy`, `SelectionStrategy`, …) implements `DrawingStrategy`, swapped at runtime when the user picks a tool |
| **Command pattern** | `com.paintapp.paint.command.pattern` | `Command` interface plus `AddCommand`, `Move`, `Copy`, `Cut`, `Paste`, `Delete`, all managed by `CommandManager` to provide undo / redo |
| **Visitor pattern** | `com.paintapp.paint.persistence` | `SaveVisitor` / `SaveToFileVisitor` serialize each shape to text without the shapes knowing the file format; `PaintFileSaver` and `PaintFileParser` handle file I/O |

The entry point is `com.paintapp.paint.Paint`, which constructs a `PaintModel` and hands it to a `View`.

## Project layout

```
PaintApp/
├── pom.xml                       Maven build + JavaFX plugin
├── mvnw, mvnw.cmd, .mvn/         Maven wrapper (no global Maven required)
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── com/paintapp/
    │       ├── paint/
    │       │   ├── Paint.java            Application entry point
    │       │   ├── app/                  Model + View + Controller
    │       │   ├── shapes/               Drawable shapes + ShapeFactory
    │       │   ├── strategy/             Drawing strategies (per tool)
    │       │   ├── command/pattern/      Undo/redo + clipboard commands
    │       │   └── persistence/          Save / load (Visitor + parser)
    │       └── scribble/                 Standalone scribble demo panel
    └── resources/
        └── icons/                        Toolbar icons (PNG)
```

## Requirements

- **JDK 22** or newer
- No need to install Maven — the project ships with the Maven wrapper (`mvnw`)
- JavaFX 22 is pulled in as a Maven dependency, so you do **not** need a separate JavaFX SDK install

## Build & run

From the project root:

```bash
# macOS / Linux
./mvnw clean javafx:run

# Windows
mvnw.cmd clean javafx:run
```

To just compile without launching:

```bash
./mvnw clean compile
```

To produce a runnable image with `jlink`:

```bash
./mvnw clean javafx:jlink
```

The generated app image will be under `target/app/`.

## Usage

1. Launch the app — a window titled **Paint** opens with a left-hand tool palette and a top menu bar.
2. Pick a shape from the left palette to set the active drawing tool.
3. Click and drag on the canvas to draw. For Polyline, click multiple times and double-click to finish.
4. Use the **Edit** menu for Cut / Copy / Paste / Delete and Undo / Redo.
5. Use **File → Import Image** to drop an image onto the canvas; it can then be moved like any other shape.
6. Use **File → Save** to write your work to a `.txt` / `.paint` file, **File → Open** to load one back, and **File → New** to clear the canvas.

## Tests

Unit tests live under `src/test/java`. Run them with:

```bash
./mvnw test
```

The current test suite covers the `PaintFileParser` against a collection of well-formed and intentionally malformed sample files in `src/test/resources/samplefiles/`.

## License

This project is released under the MIT License — see the `LICENSE` file if present, or feel free to add one.
