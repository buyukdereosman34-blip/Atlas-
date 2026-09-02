package com.atlas.app.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellCommandExecutor {

    private String workingDirectory;

    public ShellCommandExecutor(String initialDirectory) {
        this.workingDirectory = initialDirectory;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Result execute(String command) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "sh",
                    "-c",
                    command
            );

            processBuilder.directory(
                    new java.io.File(workingDirectory)
            );

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            StringBuilder output = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            return new Result(
                    output.toString(),
                    exitCode
            );

        } catch (Exception e) {

            return new Result(
                    "Shell error: " + e.getMessage(),
                    -1
            );
        }
    }

    public static class Result {

        public final String output;
        public final int exitCode;

        public Result(String output, int exitCode) {
            this.output = output;
            this.exitCode = exitCode;
        }
    }
}
