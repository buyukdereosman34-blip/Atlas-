package com.atlas.app.codegen;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleBasedCodeGenerator
        implements CodeGenerator {

    private static final Pattern REPLACE_PATTERN =
            Pattern.compile(
                    "replace\\s+(.+?)\\s+(?:with|by)\\s+(.+?)(?:\\.|$)",
                    Pattern.CASE_INSENSITIVE
            );

    @Override
    public CodeChangeSet generate(
            CodeGenerationRequest request
    ) {
        if (request == null) {
            return new CodeChangeSet(null);
        }

        String description =
                request.getTaskDescription();

        if (description == null
                || description.trim().isEmpty()) {
            return new CodeChangeSet(null);
        }

        Matcher matcher =
                REPLACE_PATTERN.matcher(
                        description.trim()
                );

        if (!matcher.find()) {
            return new CodeChangeSet(null);
        }

        String oldValue =
                normalizeValue(matcher.group(1));

        String newValue =
                normalizeValue(matcher.group(2));

        if (oldValue.isEmpty()
                || newValue.isEmpty()
                || oldValue.equals(newValue)) {
            return new CodeChangeSet(null);
        }

        Map<String, String> changes =
                new LinkedHashMap<>();

        List<String> affectedFiles =
                request.getAffectedFiles();

        if (affectedFiles == null
                || affectedFiles.isEmpty()) {
            return new CodeChangeSet(null);
        }

        File sourceRoot =
                new File(request.getSourcePath());

        if (!sourceRoot.isDirectory()) {
            return new CodeChangeSet(null);
        }

        for (String affectedFile : affectedFiles) {
            if (affectedFile == null
                    || affectedFile.trim().isEmpty()) {
                continue;
            }

            File file =
                    findFile(
                            sourceRoot,
                            affectedFile.trim()
                    );

            if (file == null) {
                continue;
            }

            try {
                String content =
                        new String(
                                Files.readAllBytes(
                                        file.toPath()
                                ),
                                StandardCharsets.UTF_8
                        );

                String changed =
                        replaceLiteral(
                                content,
                                oldValue,
                                newValue
                        );

                if (changed.equals(content)) {
                    continue;
                }

                String rootPath =
                        sourceRoot
                                .getCanonicalPath();

                String relativePath =
                        file.getCanonicalPath()
                                .substring(
                                        rootPath.length()
                                )
                                .replace(
                                        File.separatorChar,
                                        '/'
                                );

                while (relativePath.startsWith("/")) {
                    relativePath =
                            relativePath.substring(1);
                }

                changes.put(
                        relativePath,
                        changed
                );

            } catch (Exception ignored) {
                // Ignore files that cannot be safely read.
            }
        }

        return new CodeChangeSet(changes);
    }

    private String normalizeValue(String value) {
        if (value == null) {
            return "";
        }

        String result =
                value.trim();

        while (result.endsWith(".")) {
            result =
                    result.substring(
                            0,
                            result.length() - 1
                    ).trim();
        }

        if (result.length() >= 2
                && result.startsWith("\"")
                && result.endsWith("\"")) {
            result =
                    result.substring(
                            1,
                            result.length() - 1
                    );
        }

        if (result.length() >= 2
                && result.startsWith("'")
                && result.endsWith("'")) {
            result =
                    result.substring(
                            1,
                            result.length() - 1
                    );
        }

        return result;
    }

    private String replaceLiteral(
            String content,
            String oldValue,
            String newValue
    ) {
        if (oldValue.isEmpty() || newValue.isEmpty()) {
            return content;
        }

        String changed =
                content.replace(
                        "\"" + oldValue + "\"",
                        "\"" + newValue + "\""
                );

        if (!changed.equals(content)) {
            return changed;
        }

        String oldToken =
                extractPrimaryValue(oldValue);

        String newToken =
                extractPrimaryValue(newValue);

        if (oldToken.isEmpty() || newToken.isEmpty()) {
            return content;
        }

        return content.replace(
                "\"" + oldToken + "\"",
                "\"" + newToken + "\""
        );
    }

    private String extractPrimaryValue(String value) {
        if (value == null) {
            return "";
        }

        String result = value.trim();

        String lower = result.toLowerCase();

        if (lower.endsWith(" value")) {
            result =
                    result.substring(
                            0,
                            result.length() - 6
                    ).trim();
        }

        if (lower.endsWith(" value.")) {
            result =
                    result.substring(
                            0,
                            result.length() - 7
                    ).trim();
        }

        return result;
    }

    private File findFile(
            File root,
            String fileName
    ) {
        if (root == null || !root.exists()) {
            return null;
        }

        if (root.isFile()) {
            return root.getName().equals(fileName)
                    ? root
                    : null;
        }

        File[] children =
                root.listFiles();

        if (children == null) {
            return null;
        }

        for (File child : children) {
            File found =
                    findFile(
                            child,
                            fileName
                    );

            if (found != null) {
                return found;
            }
        }

        return null;
    }
}
