package com.atlas.app.improvement;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ImprovementSelfDevelopmentLoopTest {

    @Test
    public void selfDevelopmentLoopCompletesEndToEnd() throws Exception {

        Path root =
                Files.createTempDirectory(
                        "atlas-self-development-"
                );

        File source =
                new File(root.toFile(), "source");

        File labRoot =
                new File(root.toFile(), "lab");

        File target =
                new File(root.toFile(), "target");

        assertTrue(source.mkdirs());
        assertTrue(labRoot.mkdirs());
        assertTrue(target.mkdirs());

        createAndroidProject(source);

        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod method =
                new ImprovementMethod(
                        "test-method",
                        "Apply a controlled source change",
                        "test",
                        "LAB",
                        "LOW"
                );

        store.add(method);

        ImprovementTask task =
                new ImprovementTask(
                        "Change Example value",
                        "Change the Example value from old to new"
                );

        ImprovementFinding finding =
                new ImprovementFinding(
                        "Example.java",
                        "The value is outdated",
                        "Change old to new"
                );

        ImprovementSelfDevelopmentLoop loop =
                new ImprovementSelfDevelopmentLoop(
                        store
                );

        ImprovementLabChangeApplier applier =
                new ImprovementRealLabChangeApplier();

        ImprovementSelfDevelopmentResult result =
                loop.execute(
                        task,
                        finding,
                        Arrays.asList("Example.java"),
                        source.getAbsolutePath(),
                        labRoot.getAbsolutePath(),
                        target.getAbsolutePath(),
                        applier
                );

        assertNotNull(result);

        assertTrue(
                "Self-development loop failed: "
                        + result.getStage()
                        + " / "
                        + result.getMessage()
                        + " / BUILD="
                        + (
                            result.getBuildResult() == null
                                ? "null"
                                : result.getBuildResult().getBuildOutput()
                        ),
                result.isSuccess()
        );

        assertEquals(
                "COMPLETED",
                result.getStage()
        );

        assertNotNull(result.getMethod());
        assertNotNull(result.getProposal());
        assertNotNull(result.getWorkspace());
        assertNotNull(result.getBuildResult());
        assertNotNull(result.getEvaluationResult());
        assertNotNull(result.getPromotionResult());
        assertNotNull(result.getLearningResult());

        assertTrue(
                result.getBuildResult().isSuccessful()
        );

        assertTrue(
                result.getEvaluationResult().isPassed()
        );

        assertTrue(
                result.getPromotionResult().isSuccess()
        );

        assertTrue(
                result.getLearningResult().isRecorded()
        );

        File promotedFile =
                new File(
                        target,
                        "app/src/main/java/com/example/test/Example.java"
                );

        assertTrue(
                promotedFile.exists()
        );

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

        assertFalse(
                promoted.contains("\"old\"")
        );

        assertEquals(
                1,
                method.getSuccessCount()
        );

        assertEquals(
                0,
                method.getFailureCount()
        );
    }


    @Test
    public void selfDevelopmentLoopRetriesAfterEvaluationFailure() throws Exception {

        Path root =
                Files.createTempDirectory(
                        "atlas-self-development-retry-"
                );

        File source =
                new File(root.toFile(), "source");

        File labRoot =
                new File(root.toFile(), "lab");

        File target =
                new File(root.toFile(), "target");

        assertTrue(source.mkdirs());
        assertTrue(labRoot.mkdirs());
        assertTrue(target.mkdirs());

        createAndroidProject(source);

        ImprovementMethodStore store =
                new ImprovementMethodStore();

        ImprovementMethod method =
                new ImprovementMethod(
                        "retry-method",
                        "Retry a failed controlled source change",
                        "test",
                        "LAB",
                        "LOW"
                );

        store.add(method);

        ImprovementTask task =
                new ImprovementTask(
                        "Change Example value",
                        "Change the Example value from old to new"
                );

        ImprovementFinding finding =
                new ImprovementFinding(
                        "Example.java",
                        "The value is outdated",
                        "Change old to new"
                );

        final int[] fixCalls = {0};

        ImprovementFixer fixer =
                (
                        failedTask,
                        failedFinding,
                        failedProposal,
                        buildResult,
                        evaluationResult
                ) -> {
                    fixCalls[0]++;

                    return new ImprovementChangeProposal(
                            "Fix Example value",
                            "Retry after failed validation",
                            Arrays.asList("Example.java"),
                            "The value is new",
                            "LOW",
                            true,
                            new ImprovementValidation(
                                    "Build and test the corrected change",
                                    "The corrected change passes validation"
                            ),
                            java.util.Collections.singletonMap(
                                    "app/src/main/java/com/example/test/Example.java",
                                    "package com.example.test;\n\n"
                                            + "public class Example {\n"
                                            + "    public static String value() {\n"
                                            + "        return \"new\";\n"
                                            + "    }\n"
                                            + "}\n"
                            )
                    );
                };

        ImprovementSelfDevelopmentLoop loop =
                new ImprovementSelfDevelopmentLoop(
                        store,
                        null,
                        fixer
                );

        final int[] applyCalls = {0};

        ImprovementLabChangeApplier realApplier =
                new ImprovementRealLabChangeApplier();

        ImprovementLabChangeApplier applier =
                (proposal, workspace) -> {
                    boolean applied =
                            realApplier.apply(
                                    proposal,
                                    workspace
                            );

                    applyCalls[0]++;

                    if (applied && applyCalls[0] == 1) {
                        File brokenFile =
                                new File(
                                        workspace.getWorkspacePath(),
                                        "app/src/main/java/com/example/test/Example.java"
                                );

                        try {
                            write(
                                    brokenFile,
                                    "package com.example.test;\n\n"
                                            + "public class Example {\n"
                                            + "    public static String value() {\n"
                                            + "        return ;\n"
                                            + "    }\n"
                                            + "}\n"
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return applied;
                };

        ImprovementSelfDevelopmentResult result =
                loop.execute(
                        task,
                        finding,
                        Arrays.asList("Example.java"),
                        source.getAbsolutePath(),
                        labRoot.getAbsolutePath(),
                        target.getAbsolutePath(),
                        applier
                );

        assertNotNull(result);

        assertTrue(
                "Retry loop failed: "
                        + result.getStage()
                        + " / "
                        + result.getMessage(),
                result.isSuccess()
        );

        assertEquals(
                "COMPLETED",
                result.getStage()
        );

        assertTrue(
                "Expected the second attempt to succeed.",
                result.getMessage().contains("attempt 2")
        );

        assertEquals(
                1,
                fixCalls[0]
        );

        assertNotNull(result.getProposal());
        assertNotNull(result.getBuildResult());
        assertNotNull(result.getEvaluationResult());
        assertNotNull(result.getPromotionResult());
        assertNotNull(result.getLearningResult());

        assertTrue(
                result.getBuildResult().isSuccessful()
        );

        assertTrue(
                result.getEvaluationResult().isPassed()
        );

        assertTrue(
                result.getPromotionResult().isSuccess()
        );

        assertTrue(
                result.getLearningResult().isRecorded()
        );

        File promotedFile =
                new File(
                        target,
                        "app/src/main/java/com/example/test/Example.java"
                );

        assertTrue(
                promotedFile.exists()
        );

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

        assertFalse(
                promoted.contains("\"old\"")
        );

        assertEquals(
                1,
                method.getSuccessCount()
        );

        assertEquals(
                0,
                method.getFailureCount()
        );
    }

    private void createAndroidProject(
            File source
    ) throws Exception {

        File app =
                new File(
                        source,
                        "app"
                );

        File javaDir =
                new File(
                        app,
                        "src/main/java/com/example/test"
                );

        assertTrue(
                javaDir.mkdirs()
        );

        write(
                new File(
                        source,
                        "settings.gradle"
                ),
                "pluginManagement {\n" +
                "    repositories {\n" +
                "        google()\n" +
                "        mavenCentral()\n" +
                "        gradlePluginPortal()\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "dependencyResolutionManagement {\n" +
                "    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)\n" +
                "    repositories {\n" +
                "        google()\n" +
                "        mavenCentral()\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "rootProject.name = 'SelfDevelopmentTest'\n" +
                "include ':app'\n"
        );

        write(
                new File(
                        source,
                        "build.gradle"
                ),
                "plugins {\n" +
                "    id 'com.android.application' version '9.0.0' apply false\n" +
                "}\n"
        );

        write(
                new File(
                        source,
                        "gradle.properties"
                ),
                "android.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2\n" +
                "android.aapt2.useDaemon=false\n"
        );

        write(
                new File(
                        app,
                        "build.gradle"
                ),
                "plugins {\n" +
                "    id 'com.android.application'\n" +
                "}\n" +
                "\n" +
                "android {\n" +
                "    namespace 'com.example.test'\n" +
                "    compileSdk 35\n" +
                "\n" +
                "    defaultConfig {\n" +
                "        applicationId 'com.example.test'\n" +
                "        minSdk 26\n" +
                "        targetSdk 35\n" +
                "        versionCode 1\n" +
                "        versionName '1.0'\n" +
                "    }\n" +
                "\n" +
                "    compileOptions {\n" +
                "        sourceCompatibility JavaVersion.VERSION_17\n" +
                "        targetCompatibility JavaVersion.VERSION_17\n" +
                "    }\n" +
                "}\n"
        );

        write(
                new File(
                        javaDir,
                        "Example.java"
                ),
                "package com.example.test;\n" +
                "\n" +
                "public class Example {\n" +
                "    public static String value() {\n" +
                "        return \"old\";\n" +
                "    }\n" +
                "}\n"
        );

        write(
                new File(
                        app,
                        "src/main/AndroidManifest.xml"
                ),
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
                "    <application\n" +
                "        android:theme=\"@android:style/Theme.Material.Light.NoActionBar\"\n" +
                "        android:label=\"SelfDevelopmentTest\" />\n" +
                "</manifest>\n"
        );
    }

    private void write(
            File file,
            String content
    ) throws Exception {

        File parent =
                file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (FileWriter writer =
                     new FileWriter(file)) {

            writer.write(content);
        }
    }
}
