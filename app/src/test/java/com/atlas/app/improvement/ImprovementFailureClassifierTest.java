package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImprovementFailureClassifierTest {

    private final ImprovementFailureClassifier classifier =
            new ImprovementFailureClassifier();

    @Test
    public void classifyNullResultReturnsUnknown() {
        ImprovementFailureClassification result =
                classifier.classify((ImprovementLabBuildResult) null);

        assertNotNull(result);
        assertEquals(
                ImprovementFailureCategory.UNKNOWN,
                result.getCategory()
        );
    }

    @Test
    public void classifySuccessfulResultReturnsNull() {
        ImprovementLabBuildResult success =
                new ImprovementLabBuildResult(
                        true, true, 0, 0, "", ""
                );

        assertNull(classifier.classify(success));
    }

    @Test
    public void classifyAapt2DaemonFailureAsToolchain() {
        String output =
                "> Task :app:processDebugResources FAILED\n"
                        + "AAPT2 bin Daemon #0: Daemon startup failed\n"
                        + "BUILD FAILED in 1m";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.TOOLCHAIN,
                result.getCategory()
        );
        assertTrue(result.getEvidence().contains("AAPT2"));
    }

    @Test
    public void classifyUnknownSignalAsEnvironment() {
        String output = "Unknown signal 36\nSome other text";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.ENVIRONMENT,
                result.getCategory()
        );
    }

    @Test
    public void classifyPermissionDeniedAsPermission() {
        String output =
                "cp: Permission denied: /data/foo\n"
                        + "BUILD FAILED";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.PERMISSION,
                result.getCategory()
        );
    }

    @Test
    public void classifyDependencyResolutionAsDependency() {
        String output =
                "Could not resolve all files for configuration "
                        + "':app:debugRuntimeClasspath'.\n"
                        + "Failed to resolve: androidx.appcompat";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.DEPENDENCY,
                result.getCategory()
        );
    }

    @Test
    public void classifyJavaCompileErrorAsCode() {
        String output =
                "error: cannot find symbol\n"
                        + "  symbol:   class Foo\n"
                        + "  location: class Bar\n"
                        + "1 error";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.CODE,
                result.getCategory()
        );
    }

    @Test
    public void classifyUnclosedStringLiteralAsCode() {
        String output =
                "Sample.java:5: error: unclosed string literal\n"
                        + "    return new;\n"
                        + "                ^";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.CODE,
                result.getCategory()
        );
    }

    @Test
    public void classifyAssertionErrorAsTest() {
        String output =
                "There were failing tests. See the report at:\n"
                        + "  file:///path/to/report.html\n"
                        + "java.lang.AssertionError: expected success";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.TEST,
                result.getCategory()
        );
    }

    @Test
    public void classifyEmptyOutputAsUnknown() {
        ImprovementFailureClassification result =
                classifier.classify("");

        assertEquals(
                ImprovementFailureCategory.UNKNOWN,
                result.getCategory()
        );
    }

    @Test
    public void classifyUnrecognizedPatternAsUnknown() {
        String output =
                "Something totally unknown happened here";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.UNKNOWN,
                result.getCategory()
        );
    }

    @Test
    public void classifyGenericGradleTaskFailureAsBuild() {
        String output =
                "> Task :app:someTask FAILED\n"
                        + "Execution failed for task ':app:someTask'.\n"
                        + "BUILD FAILED in 2s";

        ImprovementFailureClassification result =
                classifier.classify(output);

        assertEquals(
                ImprovementFailureCategory.BUILD,
                result.getCategory()
        );
    }

    @Test
    public void toolchainIssueIsMarkedAsEnvironmentIssue() {
        ImprovementFailureClassification result =
                classifier.classify(
                        "AAPT2 bin Daemon #0: Daemon startup failed"
                );

        assertTrue(result.isEnvironmentIssue());
    }

    @Test
    public void toolchainIssueIsNotRetryable() {
        ImprovementFailureClassification result =
                classifier.classify(
                        "AAPT2 bin Daemon #0: Daemon startup failed"
                );

        assertFalse(result.isRetryable());
    }

    @Test
    public void codeIssueIsRetryable() {
        ImprovementFailureClassification result =
                classifier.classify("error: cannot find symbol");

        assertTrue(result.isRetryable());
        assertFalse(result.isEnvironmentIssue());
    }

    @Test
    public void classifyFullResultUsesBothOutputs() {
        ImprovementLabBuildResult result =
                new ImprovementLabBuildResult(
                        true,
                        false,
                        0,
                        1,
                        "BUILD SUCCESSFUL",
                        "There were failing tests\nAssertionError: foo"
                );

        ImprovementFailureClassification classification =
                classifier.classify(result);

        assertEquals(
                ImprovementFailureCategory.TEST,
                classification.getCategory()
        );
    }
}
