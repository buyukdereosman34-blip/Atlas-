package com.atlas.app.improvement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImprovementLabBuildExecutor {

    public ImprovementLabBuildResult buildAndTest(
            ImprovementLabWorkspace workspace
    ) {
        if (workspace == null) {
            return failure(
                    "LAB workspace is missing",
                    -1
            );
        }

        Path labPath =
                Paths.get(workspace.getWorkspacePath())
                        .toAbsolutePath()
                        .normalize();

        if (!Files.isDirectory(labPath)) {
            return failure(
                    "LAB workspace does not exist",
                    -1
            );
        }

        if (!Files.exists(labPath.resolve("settings.gradle"))
                && !Files.exists(labPath.resolve("settings.gradle.kts"))) {
            return failure(
                    "LAB workspace is not a Gradle project",
                    -1
            );
        }

        CommandResult build =
                execute(
                        labPath,
                        "gradle",
                        ":app:assembleDebug"
                );

        if (!build.success) {
            return new ImprovementLabBuildResult(
                    false,
                    false,
                    build.exitCode,
                    -1,
                    build.output,
                    ""
            );
        }

        CommandResult test =
                execute(
                        labPath,
                        "gradle",
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

    private CommandResult execute(
            Path workingDirectory,
            String command,
            String argument
    ) {
        ProcessBuilder builder =
                new ProcessBuilder(
                        command,
                        argument
                );

        builder.directory(
                workingDirectory.toFile()
        );

        builder.redirectErrorStream(true);

        StringBuilder output =
                new StringBuilder();

        try {
            Process process =
                    builder.start();

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         process.getInputStream()
                                 )
                         )) {

                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line)
                            .append('\n');
                }
            }

            int exitCode =
                    process.waitFor();

            return new CommandResult(
                    exitCode == 0,
                    exitCode,
                    output.toString()
            );

        } catch (IOException e) {
            return new CommandResult(
                    false,
                    -1,
                    e.toString()
            );

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
                false,
                false,
                exitCode,
                -1,
                message,
                ""
        );
    }

    private static class CommandResult {

        private final boolean success;
        private final int exitCode;
        private final String output;

        private CommandResult(
                boolean success,
                int exitCode,
                String output
        ) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output;
        }
    }
}
