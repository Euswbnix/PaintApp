package com.paintapp.paint.persistence;

import com.paintapp.paint.app.PaintModel;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link PaintFileParser}. Sample files live in
 * {@code src/test/resources/samplefiles/}.
 *
 * For files that should fail to parse, only the error line number is
 * checked (not the full message text), so that the precise wording of
 * the error is free to change.
 */
public class PaintFileParserTest {

    private static final Pattern ERROR_LINE_PATTERN =
            Pattern.compile("^Error in line\\s+(\\d+)\\s+");

    private void doParserTestCase(String fileName, String description,
                                  String expectedErrorMessage) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String fullFileName = classLoader.getResource(fileName).getFile();

        try (BufferedReader in = new BufferedReader(new FileReader(fullFileName))) {
            PaintFileParser parser = new PaintFileParser();
            PaintModel model = new PaintModel();
            boolean ok = parser.parse(in, model);
            String errorMessage = parser.getErrorMessage();

            if (expectedErrorMessage.isEmpty()) {
                assertTrue(ok, fileName + ": " + description + " (got error: " + errorMessage + ")");
                assertEquals("", errorMessage, fileName + ": no error message expected");
            } else {
                assertFalse(ok, fileName + ": " + description);
                String expectedLine = extractLine(expectedErrorMessage);
                String reportedLine = extractLine(errorMessage);
                assertEquals(expectedLine, reportedLine,
                        fileName + ": error reported on wrong line (full message: " + errorMessage + ")");
            }
        }
    }

    private String extractLine(String message) {
        Matcher m = ERROR_LINE_PATTERN.matcher(message);
        return m.find() ? m.group(1) : "";
    }

    @Test
    void parserTest1() throws IOException {
        doParserTestCase("samplefiles/basic_nospace.txt",
                "Returns true for basic file with no spaces", "");
    }

    @Test
    void parserTest2() throws IOException {
        doParserTestCase("samplefiles/basic_spaces.txt",
                "Returns true for basic file with spaces", "");
    }

    @Test
    void parserTest3() throws IOException {
        doParserTestCase("samplefiles/basic_multispaces.txt",
                "Returns true for basic file with multiple spaces", "");
    }

    @Test
    void parserTest4() throws IOException {
        doParserTestCase("samplefiles/basic_fail.txt",
                "Returns false for basic incorrect file format",
                "Error in line 1 ");
    }

    @Test
    void parserTest5() throws IOException {
        doParserTestCase("samplefiles/circle_single.txt",
                "Returns true for file with one circle", "");
    }

    @Test
    void parserTest6() throws IOException {
        doParserTestCase("samplefiles/circle_multi.txt",
                "Returns true for file with multiple circles", "");
    }

    @Test
    void parserTest7() throws IOException {
        doParserTestCase("samplefiles/circle_fail_values.txt",
                "Returns false for circle with incorrect values",
                "Error in line 3 ");
    }

    @Test
    void parserTest8() throws IOException {
        doParserTestCase("samplefiles/circle_fail_wrongend.txt",
                "Returns false for circle with wrong end",
                "Error in line 7 ");
    }

    @Test
    void parserTest9() throws IOException {
        doParserTestCase("samplefiles/circle_fail_wrongorder.txt",
                "Returns false for circle with properties in wrong order",
                "Error in line 3 ");
    }

    @Test
    void parserTest10() throws IOException {
        doParserTestCase("samplefiles/rectangle_single.txt",
                "Returns true for file with one rect", "");
    }

    @Test
    void parserTest11() throws IOException {
        doParserTestCase("samplefiles/rectangle_multi.txt",
                "Returns true for file with multiple rects", "");
    }

    @Test
    void parserTest12() throws IOException {
        doParserTestCase("samplefiles/rectangle_wrongorder.txt",
                "Returns false for rect with properties in wrong order",
                "Error in line 5 ");
    }

    @Test
    void parserTest13() throws IOException {
        doParserTestCase("samplefiles/squiggle_single.txt",
                "Returns true for file with one squiggle", "");
    }

    @Test
    void parserTest14() throws IOException {
        doParserTestCase("samplefiles/multishapes.txt",
                "Returns true for file with multiple shapes", "");
    }

    @Test
    void parserTest15() throws IOException {
        doParserTestCase("samplefiles/multishapes_fail_missingend.txt",
                "Returns false for multiple shapes file with extra content after end",
                "Error in line 48 ");
    }

    @Test
    void parserTest16() throws IOException {
        doParserTestCase("samplefiles/multishapes_fail_missingendshape.txt",
                "Returns false for multiple shapes file with incorrect end shape",
                "Error in line 13 ");
    }
}
