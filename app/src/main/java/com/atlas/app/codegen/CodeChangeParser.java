package com.atlas.app.codegen;

import java.util.LinkedHashMap;
import java.util.Map;

public class CodeChangeParser {

    public CodeChangeSet parse(String output) {
        if (output == null || output.trim().isEmpty()) {
            return new CodeChangeSet(null);
        }

        Map<String, String> changes =
                new LinkedHashMap<>();

        String[] blocks =
                output.split(
                        "(?m)^---FILE---\\s*$"
                );

        for (String block : blocks) {
            if (block == null || block.trim().isEmpty()) {
                continue;
            }

            int separator =
                    block.indexOf("\n---CONTENT---\n");

            if (separator < 0) {
                continue;
            }

            String path =
                    block.substring(
                            0,
                            separator
                    ).trim();

            String content =
                    block.substring(
                            separator
                                    + "\n---CONTENT---\n"
                                            .length()
                    );

            if (path.isEmpty()) {
                continue;
            }

            changes.put(path, content);
        }

        return new CodeChangeSet(changes);
    }
}
