package com.atlas.app.codegen;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodeChangeSet {

    private final Map<String, String> fileContents;

    public CodeChangeSet(Map<String, String> fileContents) {
        this.fileContents =
                fileContents == null
                        ? Collections.emptyMap()
                        : Collections.unmodifiableMap(
                                new LinkedHashMap<>(fileContents)
                        );
    }

    public Map<String, String> getFileContents() {
        return fileContents;
    }

    public boolean isEmpty() {
        return fileContents.isEmpty();
    }
}
