package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImprovementFailureClassifier {

    private final List<Rule> rules;

    public ImprovementFailureClassifier() {
        this.rules = buildRules();
    }

    /**
     * Classify a LAB build result.
     * Returns null if the result indicates success (no failure to classify).
     */
    public ImprovementFailureClassification classify(
            ImprovementLabBuildResult result
    ) {
        if (result == null) {
            return new ImprovementFailureClassification(
                    ImprovementFailureCategory.UNKNOWN,
                    "LAB build result is missing",
                    ""
            );
        }

        if (result.isSuccessful()) {
            return null;
        }

        StringBuilder combined = new StringBuilder();

        if (result.getBuildOutput() != null) {
            combined.append(result.getBuildOutput()).append('\n');
        }

        if (result.getTestOutput() != null) {
            combined.append(result.getTestOutput()).append('\n');
        }

        return classify(combined.toString());
    }

    /**
     * Classify a raw failure output.
     * Returns a classification; never null.
     */
    public ImprovementFailureClassification classify(String output) {
        if (output == null || output.trim().isEmpty()) {
            return new ImprovementFailureClassification(
                    ImprovementFailureCategory.UNKNOWN,
                    "Failure output is empty",
                    ""
            );
        }

        for (Rule rule : rules) {
            Matcher matcher = rule.pattern.matcher(output);

            if (matcher.find()) {
                String evidence = extractEvidence(output, matcher.start());

                return new ImprovementFailureClassification(
                        rule.category,
                        rule.label,
                        evidence
                );
            }
        }

        return new ImprovementFailureClassification(
                ImprovementFailureCategory.UNKNOWN,
                "No known failure pattern matched",
                ""
        );
    }

    private String extractEvidence(String output, int position) {
        int lineStart = output.lastIndexOf('\n', position) + 1;

        int lineEnd = output.indexOf('\n', position);

        if (lineEnd == -1) {
            lineEnd = output.length();
        }

        String line = output.substring(lineStart, lineEnd).trim();

        if (line.length() > 200) {
            line = line.substring(0, 200) + "...";
        }

        return line;
    }

    private static List<Rule> buildRules() {
        List<Rule> list = new ArrayList<>();

        // --- TOOLCHAIN ---
        list.add(new Rule(
                Pattern.compile("AAPT2.*Daemon.*startup failed", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TOOLCHAIN,
                "AAPT2 daemon failed to start"
        ));
        list.add(new Rule(
                Pattern.compile("AAPT2.*daemon", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TOOLCHAIN,
                "AAPT2 daemon error"
        ));
        list.add(new Rule(
                Pattern.compile("Failed to transform .*\\.aar", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TOOLCHAIN,
                "AAR transform failed"
        ));
        list.add(new Rule(
                Pattern.compile("Android Gradle plugin.*requires", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TOOLCHAIN,
                "AGP version incompatibility"
        ));

        // --- DEPENDENCY ---
        list.add(new Rule(
                Pattern.compile("Could not resolve (all files for )?configuration", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.DEPENDENCY,
                "Dependency resolution failed"
        ));
        list.add(new Rule(
                Pattern.compile("Could not find .*:", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.DEPENDENCY,
                "Dependency not found"
        ));
        list.add(new Rule(
                Pattern.compile("Failed to resolve:", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.DEPENDENCY,
                "Failed to resolve dependency"
        ));

        // --- PERMISSION ---
        list.add(new Rule(
                Pattern.compile("Permission denied", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.PERMISSION,
                "Permission denied"
        ));
        list.add(new Rule(
                Pattern.compile("EACCES", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.PERMISSION,
                "EACCES (access denied)"
        ));

        // --- ENVIRONMENT ---
        list.add(new Rule(
                Pattern.compile("Unknown signal \\d+"),
                ImprovementFailureCategory.ENVIRONMENT,
                "Process killed by unknown signal"
        ));
        list.add(new Rule(
                Pattern.compile("Unable to access jarfile", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.ENVIRONMENT,
                "JAR file not accessible"
        ));
        list.add(new Rule(
                Pattern.compile("OutOfMemoryError", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.ENVIRONMENT,
                "Out of memory"
        ));
        list.add(new Rule(
                Pattern.compile("No space left on device", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.ENVIRONMENT,
                "Disk full"
        ));

        // --- CODE ---
        list.add(new Rule(
                Pattern.compile("\\.java:\\d+.*error:", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.CODE,
                "Java compilation error"
        ));
        list.add(new Rule(
                Pattern.compile("\\.kt:\\d+.*error:", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.CODE,
                "Kotlin compilation error"
        ));
        list.add(new Rule(
                Pattern.compile("cannot find symbol", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.CODE,
                "Symbol not found"
        ));
        list.add(new Rule(
                Pattern.compile("unclosed string literal", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.CODE,
                "Syntax error: unclosed string"
        ));
        list.add(new Rule(
                Pattern.compile("';' expected", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.CODE,
                "Syntax error: missing semicolon"
        ));
        list.add(new Rule(
                Pattern.compile("illegal start of expression", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.CODE,
                "Syntax error"
        ));

        // --- TEST ---
        list.add(new Rule(
                Pattern.compile("AssertionError", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TEST,
                "Test assertion failed"
        ));
        list.add(new Rule(
                Pattern.compile("Test failed", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TEST,
                "Test failed"
        ));
        list.add(new Rule(
                Pattern.compile("There were failing tests", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.TEST,
                "Test suite has failures"
        ));

        // --- BUILD (generic gradle failure, last resort) ---
        list.add(new Rule(
                Pattern.compile("Execution failed for task ':", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.BUILD,
                "Gradle task failed"
        ));
        list.add(new Rule(
                Pattern.compile("BUILD FAILED", Pattern.CASE_INSENSITIVE),
                ImprovementFailureCategory.BUILD,
                "Gradle build failed"
        ));

        return list;
    }

    private static class Rule {

        final Pattern pattern;
        final ImprovementFailureCategory category;
        final String label;

        Rule(
                Pattern pattern,
                ImprovementFailureCategory category,
                String label
        ) {
            this.pattern = pattern;
            this.category = category;
            this.label = label;
        }
    }
}
