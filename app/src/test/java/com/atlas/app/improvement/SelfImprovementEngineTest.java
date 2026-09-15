package com.atlas.app.improvement;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class SelfImprovementEngineTest {

    @Test
    public void analyzeObservationCreatesProposal() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Tool execution failed",
                        "MEDIUM"
                );

        ImprovementProposal proposal =
                engine.analyze(observation);

        assertNotNull(proposal);
        assertEquals(
                "Investigate: Tool execution failed",
                proposal.getTitle()
        );
    }

    @Test
    public void analyzeNullObservationReturnsNull() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        assertEquals(
                null,
                engine.analyze(null)
        );
    }

    @Test
    public void observeStoresObservation() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementObservation observation =
                new ImprovementObservation(
                        "runtime",
                        "Failure",
                        "MEDIUM"
                );

        engine.observe(observation);

        List<ImprovementObservation> observations =
                engine.getObservations();

        assertEquals(1, observations.size());
        assertSame(observation, observations.get(0));
    }

    @Test
    public void observeIgnoresNullObservation() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        engine.observe(null);

        assertEquals(
                0,
                engine.getObservations().size()
        );
    }

    @Test
    public void analyzesTaskThroughMethodStore() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementMethod method =
                new ImprovementMethod(
                        "Runtime inspection",
                        "Inspect runtime",
                        "ATLAS",
                        "source access",
                        "LOW"
                );

        engine.addMethod(method);

        ImprovementFinding finding =
                engine.analyzeTask(
                        new ImprovementTask(
                                "Improve Runtime",
                                "Find a runtime improvement"
                        )
                );

        assertNotNull(finding);
        assertEquals(
                "Use method: Runtime inspection",
                finding.getRecommendation()
        );
    }

    @Test
    public void executesTaskWithMethodFallback() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementMethod first =
                new ImprovementMethod(
                        "First",
                        "First method",
                        "test",
                        "none",
                        "LOW"
                );

        ImprovementMethod second =
                new ImprovementMethod(
                        "Second",
                        "Second method",
                        "test",
                        "none",
                        "LOW"
                );

        first.recordSuccess();

        engine.addMethod(first);
        engine.addMethod(second);

        ImprovementMethod result =
                engine.executeTask(
                        new ImprovementTask(
                                "Test fallback",
                                "Test method fallback"
                        ),
                        method -> method == second
                );

        assertSame(second, result);
        assertEquals(1, first.getFailureCount());
        assertEquals(1, second.getSuccessCount());
    }

    @Test
    public void createsChangeProposalFromTaskAndFinding() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementTask task =
                new ImprovementTask(
                        "Improve runtime",
                        "Runtime failure needs improvement"
                );

        ImprovementFinding finding =
                new ImprovementFinding(
                        "Runtime",
                        "Tool execution failed",
                        "Improve failure handling"
                );

        ImprovementChangeProposal proposal =
                engine.proposeChange(
                        task,
                        finding,
                        java.util.Arrays.asList(
                                "AtlasRuntime.java",
                                "SelfImprovementEngine.java"
                        )
                );

        assertNotNull(proposal);
        assertEquals(
                ImprovementChangeProposalStatus.PROPOSED,
                proposal.getStatus()
        );
        assertEquals(
                "Runtime",
                proposal.getAffectedFiles().isEmpty()
                        ? null
                        : finding.getArea()
        );
        assertEquals(2, proposal.getAffectedFiles().size());
        assertEquals(
                "MEDIUM",
                proposal.getRisk()
        );
        assertTrue(proposal.isReversible());
        assertNotNull(proposal.getValidation());
    }

    @Test
    public void changeProposalCanBeApproved() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Change code",
                        "Improve behavior",
                        java.util.Collections.singletonList(
                                "Test.java"
                        ),
                        "Tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "Tests pass"
                        )
                );

        proposal.approve();

        assertEquals(
                ImprovementChangeProposalStatus.APPROVED,
                proposal.getStatus()
        );
    }

    @Test
    public void changeProposalCanBeRejected() {
        ImprovementChangeProposal proposal =
                new ImprovementChangeProposal(
                        "Change code",
                        "Improve behavior",
                        java.util.Collections.singletonList(
                                "Test.java"
                        ),
                        "Tests pass",
                        "LOW",
                        true,
                        new ImprovementValidation(
                                "Run tests",
                                "Tests pass"
                        )
                );

        proposal.reject();

        assertEquals(
                ImprovementChangeProposalStatus.REJECTED,
                proposal.getStatus()
        );
    }

    @Test
    public void executeSelfDevelopmentRunsEndToEnd()
            throws Exception {

        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        Path root =
                Files.createTempDirectory(
                        "atlas-self-development"
                );

        File source =
                root.resolve("source").toFile();

        File lab =
                root.resolve("lab").toFile();

        File target =
                root.resolve("target").toFile();

        assertTrue(source.mkdirs());
        assertTrue(lab.mkdirs());
        assertTrue(target.mkdirs());

        writeUtf8(
                new File(source, "settings.gradle"),
                "pluginManagement {\n"
                        + "    repositories {\n"
                        + "        google()\n"
                        + "        mavenCentral()\n"
                        + "        gradlePluginPortal()\n"
                        + "    }\n"
                        + "}\n"
                        + "\n"
                        + "dependencyResolutionManagement {\n"
                        + "    repositories {\n"
                        + "        google()\n"
                        + "        mavenCentral()\n"
                        + "    }\n"
                        + "}\n"
                        + "\n"
                        + "rootProject.name = 'SelfDevelopmentFixture'\n"
                        + "include ':app'\n"
        );

        writeUtf8(
                new File(source, "build.gradle"),
                "plugins {\n"
                        + "    id 'com.android.application' "
                        + "version '9.0.0' apply false\n"
                        + "}\n"
        );

        writeUtf8(
                new File(source, "gradle.properties"),
                "android.aapt2FromMavenOverride="
                        + "/data/data/com.termux/files/usr/bin/aapt2\n"
                        + "android.aapt2.useDaemon=false\n"
        );

        File app =
                new File(source, "app");

        assertTrue(app.mkdirs());

        writeUtf8(
                new File(app, "build.gradle"),
                "plugins {\n"
                        + "    id 'com.android.application'\n"
                        + "}\n"
                        + "\n"
                        + "android {\n"
                        + "    namespace 'com.example.selfdevelopment'\n"
                        + "    compileSdk 35\n"
                        + "\n"
                        + "    defaultConfig {\n"
                        + "        applicationId "
                        + "'com.example.selfdevelopment'\n"
                        + "        minSdk 26\n"
                        + "        targetSdk 35\n"
                        + "        versionCode 1\n"
                        + "        versionName '1.0'\n"
                        + "    }\n"
                        + "}\n"
                        + "\n"
                        + "dependencies {\n"
                        + "    testImplementation "
                        + "'junit:junit:4.13.2'\n"
                        + "}\n"
        );

        File manifestDir =
                new File(
                        app,
                        "src/main"
                );

        assertTrue(manifestDir.mkdirs());

        writeUtf8(
                new File(
                        manifestDir,
                        "AndroidManifest.xml"
                ),
                "<manifest "
                        + "xmlns:android="
                        + "\"http://schemas.android.com/apk/res/android\">\n"
                        + "    <application\n"
                        + "        android:theme="
                        + "\"@android:style/Theme.Material.Light\" />\n"
                        + "</manifest>\n"
        );

        File sourceDir =
                new File(
                        app,
                        "src/main/java/"
                                + "com/example/selfdevelopment"
                );

        assertTrue(sourceDir.mkdirs());

        writeUtf8(
                new File(
                        sourceDir,
                        "Sample.java"
                ),
                "package com.example.selfdevelopment;\n"
                        + "\n"
                        + "public class Sample {\n"
                        + "    String value = \"old\";\n"
                        + "}\n"
        );

        ImprovementSelfDevelopmentResult result =
                engine.executeSelfDevelopment(
                        new ImprovementTask(
                                "Improve Sample",
                                "Replace old value with new value"
                        ),
                        java.util.Collections.singletonList(
                                "Sample.java"
                        ),
                        source.getAbsolutePath(),
                        lab.getAbsolutePath(),
                        target.getAbsolutePath(),
                        new ImprovementRealLabChangeApplier()
                );

        assertNotNull(result);

        if (!result.isSuccess()) {
            String buildOutput = "";

            if (result.getBuildResult() != null) {
                buildOutput =
                        "\n\n=== LAB BUILD OUTPUT ===\n"
                                + result.getBuildResult().getBuildOutput()
                                + "\n=== LAB TEST OUTPUT ===\n"
                                + result.getBuildResult().getTestOutput();
            }

            throw new AssertionError(
                    "Self-development failed at stage "
                            + result.getStage()
                            + ": "
                            + result.getMessage()
                            + buildOutput
            );
        }

        assertEquals(
                "COMPLETED",
                result.getStage()
        );

        File promotedFile =
                new File(
                        target,
                        "app/src/main/java/"
                                + "com/example/selfdevelopment/"
                                + "Sample.java"
                );

        assertTrue(promotedFile.exists());

        String promoted =
                new String(
                        Files.readAllBytes(
                                promotedFile.toPath()
                        ),
                        StandardCharsets.UTF_8
                );

        assertTrue(
                promoted.contains("\"new\"")
        );

        assertNotNull(
                result.getLearningResult()
        );
    }

    private static void writeUtf8(
            File file,
            String content
    ) throws Exception {
        Files.write(
                file.toPath(),
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    public void taskExecutionWithoutMethodsReturnsNull() {
        SelfImprovementEngine engine =
                new SelfImprovementEngine();

        ImprovementMethod result =
                engine.executeTask(
                        new ImprovementTask(
                                "Test",
                                "No methods"
                        ),
                        method -> true
                );

        assertEquals(null, result);
    }
}
