package com.atlas.app.improvement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ImprovementLabBuildExecutor {

    private static final long TIMEOUT_MINUTES = 15L;

    public ImprovementLabBuildResult buildAndTest(
            ImprovementLabWorkspace workspace
    ) {
        if (workspace == null) {
            return failure("LAB workspace is missing", -1);
        }

        Path labPath = Paths.get(workspace.getWorkspacePath())
                .toAbsolutePath()
                .normalize();

        if (!Files.isDirectory(labPath)) {
            return failure("LAB workspace does not exist", -1);
        }

        if (!Files.exists(labPath.resolve("settings.gradle"))
                && !Files.exists(labPath.resolve("settings.gradle.kts"))) {
            return failure("LAB workspace is not a Gradle project", -1);
        }

        String gradle = resolveGradleCommand(labPath);

        CommandResult build = executeNested(
                labPath,
                gradle,
                ":app:assembleDebug"
        );

        if (!build.success) {

            // AAPT2 toolchain failure -> Java-only fallback
            if (isAapt2Failure(build.output)) {

                CommandResult javaOnly = executeNested(
                        labPath,
                        gradle,
                        ":app:compileDebugJavaWithJavac"
                );

                if (javaOnly.success) {
                    return new ImprovementLabBuildResult(
                            true,
                            false,
                            javaOnly.exitCode,
                            -1,
                            javaOnly.output
                                    + "\n\n[JAVA_ONLY_FALLBACK] "
                                    + "AAPT2 toolchain failure. "
                                    + "Only Java compilation verified. "
                                    + "Resources and tests NOT verified.",
                            "",
                            ImprovementLabValidationMode.JAVA_ONLY
                    );
                }

                return new ImprovementLabBuildResult(
                        false,
                        false,
                        javaOnly.exitCode,
                        -1,
                        "AAPT2 toolchain failure AND Java fallback failed.\n\n"
                                + javaOnly.output,
                        "",
                        ImprovementLabValidationMode.JAVA_ONLY
                );
            }

            return new ImprovementLabBuildResult(
                    false,
                    false,
                    build.exitCode,
                    -1,
                    build.output,
                    ""
            );
        }

        CommandResult test = executeNested(
                labPath,
                gradle,
                ":app:test"
        );

        return new ImprovementLabBuildResult(
                true,
                test.success,
                build.exitCode,
                test.exitCode,
                build.output,
                test.output
        );
    }

    private boolean isAapt2Failure(String output) {
        if (output == null) {
            return false;
        }
        return output.contains("AAPT2")
                && output.contains("Daemon");
    }

    private String resolveGradleCommand(Path labPath) {
        Path gradlew = labPath.resolve("gradlew");
        if (Files.isRegularFile(gradlew) && Files.isExecutable(gradlew)) {
            return gradlew.toString();
        }
        return "gradle";
    }

    private CommandResult executeNested(
            Path workingDirectory,
            String command,
            String task
    ) {
        List<String> args = new ArrayList<>();
        args.add(command);
        args.add("--no-daemon");
        args.add("--offline");
        args.add("-Dorg.gradle.daemon=false");
        args.add("-Dorg.gradle.workers.max=1");
        args.add("-Dorg.gradle.parallel=false");
        args.add("-Dorg.gradle.vfs.watch=false");
        args.add("-Dorg.gradle.configuration-cache=false");
        args.add(task);

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(workingDirectory.toFile());
        builder.redirectErrorStream(true);

        Map<String, String> env = builder.environment();

        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            env.put("JAVA_HOME", javaHome);
        }

        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            env.put("GRADLE_USER_HOME", userHome + "/.gradle");
        }

        String path = System.getenv("PATH");
        if (path != null) {
            env.put("PATH", path);
        }

        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null) {
            env.put("ANDROID_HOME", androidHome);
        }
        String androidSdkRoot = System.getenv("ANDROID_SDK_ROOT");
        if (androidSdkRoot != null) {
            env.put("ANDROID_SDK_ROOT", androidSdkRoot);
        }
        env.put("GRADLE_OPTS", "-Dorg.gradle.daemon=false");

        StringBuilder output = new StringBuilder();

        try {
        Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
            }

            if (!process.waitFor(TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                output.append("\n[LAB timed out after ")
                        .append(TIMEOUT_MINUTES)
                        .append(" minutes]\n");
                return new CommandResult(false, -1, output.toString());
            }

            int exitCode = process.exitValue();

            return new CommandResult(
                    exitCode == 0,
                    exitCode,
                    output.toString()
            );

        } catch (IOException e) {
            return new CommandResult(false, -1, e.toString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CommandResult(
                    false,
                    -1,
                    "Process interrupted: " + e
            );
        }
    }

    private ImprovementLabBuildResult failure(
            String message,
            int exitCode
    ) {
        return new ImprovementLabBuildResult(
                false, false, exitCode, -1, message, ""
        );
    }

    private static class CommandResult {
        private final boolean success;
        private final int exitCode;
        private final String output;

        private CommandResult(boolean success, int exitCode, String output) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output;
        }
    }
}
