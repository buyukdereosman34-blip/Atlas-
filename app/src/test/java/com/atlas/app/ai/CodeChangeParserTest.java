package com.atlas.app.ai;

import com.atlas.app.codegen.CodeChangeParser;
import com.atlas.app.codegen.CodeChangeSet;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class CodeChangeParserTest {

    @Test
    public void parsesStructuredFileChanges() {
        String output =
                "---FILE---\n"
                        + "src/Test.java\n"
                        + "---CONTENT---\n"
                        + "class Test {}\n"
                        + "---FILE---\n"
                        + "src/Other.java\n"
                        + "---CONTENT---\n"
                        + "class Other {}\n";

        CodeChangeSet result =
                new CodeChangeParser()
                        .parse(output);

        Map<String, String> files =
                result.getFileContents();

        assertEquals(
                2,
                files.size()
        );

        assertEquals(
                "class Test {}\n",
                files.get("src/Test.java")
        );

        assertEquals(
                "class Other {}\n",
                files.get("src/Other.java")
        );
    }

    @Test
    public void invalidOutputProducesEmptyChangeSet() {
        CodeChangeSet result =
                new CodeChangeParser()
                        .parse("not structured");

        assertTrue(
                result.isEmpty()
        );
    }
}
