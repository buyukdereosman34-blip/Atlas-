package com.atlas.app.tool.improvement;

import com.atlas.app.improvement.ImprovementRealLabChangeApplier;
import com.atlas.app.improvement.ImprovementTask;
import com.atlas.app.improvement.SelfImprovementEngine;
import com.atlas.app.improvement.ImprovementSelfDevelopmentResult;
import com.atlas.app.runtime.AtlasRuntime;
import com.atlas.app.tool.AtlasTool;
import com.atlas.app.tool.ToolResult;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelfDevelopmentTool implements AtlasTool {

    private final AtlasRuntime runtime;
    private final SelfImprovementEngine engine;

    public SelfDevelopmentTool(AtlasRuntime runtime) {
        this.runtime = runtime;
        this.engine = new SelfImprovementEngine();
    }

    @Override
    public String getName() {
        return "self_development";
    }

    @Override
    public String getDescription() {
        return "ATLAS'ın kendi kodunu LAB üzerinden güvenli şekilde geliştirme sistemi";
    }

    @Override
    public ToolResult execute(
            String action,
            String... args
    ) {
        if (action == null || action.trim().isEmpty()) {
            return ToolResult.failure(
                    "Self-development action is empty",
                    -1
            );
        }

        if (!"develop".equalsIgnoreCase(action.trim())) {
            return ToolResult.failure(
                    "Unsupported self-development action: " + action,
                    -2
            );
        }

        if (runtime == null) {
            return ToolResult.failure(
                    "ATLAS runtime is missing",
                    -3
            );
        }

        if (args == null || args.length < 3) {
            return ToolResult.failure(
                    "Usage: tool self_development develop <title> <description> <affectedFiles>",
                    -4
            );
        }

        String title = args[0];
        String description = args[1];

        List<String> affectedFiles;

        if (args[2].trim().isEmpty()) {
            affectedFiles = Collections.emptyList();
        } else {
            affectedFiles = Arrays.asList(
                    args[2].split(",")
            );
        }

        String sourcePath =
                runtime.getWorkingDirectory();

        File source =
                new File(sourcePath);

        File parent =
                source.getAbsoluteFile().getParentFile();

        File labRoot =
                new File(
                        parent,
                        "atlas-self-development-lab"
                );

        File target =
                new File(
                        parent,
                        "atlas-self-development-target"
                );

        ImprovementSelfDevelopmentResult result =
                engine.executeSelfDevelopment(
                        new ImprovementTask(title, description),
                        affectedFiles,
                        source.getAbsolutePath(),
                        labRoot.getAbsolutePath(),
                        target.getAbsolutePath(),
                        new ImprovementRealLabChangeApplier()
                );

        if (result == null) {
            return ToolResult.failure(
                    "Self-development returned no result",
                    -5
            );
        }

        String message =
                "Self-development: "
                        + result.getStage()
                        + " / "
                        + result.getMessage();

        if (result.isSuccess()) {
            return ToolResult.success(message);
        }

        return ToolResult.failure(
                message,
                -6
        );
    }
}
