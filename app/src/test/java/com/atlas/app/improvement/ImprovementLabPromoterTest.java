package com.atlas.app.improvement;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImprovementLabPromoterTest {

    @Test
    public void rejectsUnvalidatedWorkspace() throws Exception {
        Path root =
                Files.createTempDirectory("atlas-promotion-test");

        Path source =
                root.resolve("source");

        Path target =
                root.resolve("target");

        Files.createDirectories(source);
        Files.createDirectories(target);

        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-unvalidated",
                        source.toString(),
                        source.toString(),
                        "snapshot"
                );

        workspace.markReady();

        ImprovementLabPromoter promoter =
                new ImprovementLabPromoter();

        ImprovementLabPromotionResult result =
                promoter.promote(
                        workspace,
                        target.toString()
                );

        assertFalse(result.isSuccess());
        assertEquals(
                ImprovementLabStatus.READY,
                workspace.getStatus()
        );
    }

    @Test
    public void rejectsMissingTarget() throws Exception {
        Path root =
                Files.createTempDirectory("atlas-promotion-test");

        Path source =
                root.resolve("source");

        Files.createDirectories(source);

        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-no-target",
                        source.toString(),
                        source.toString(),
                        "snapshot"
                );

        workspace.markReady();
        workspace.markChanged();
        workspace.markValidated();

        ImprovementLabPromoter promoter =
                new ImprovementLabPromoter();

        ImprovementLabPromotionResult result =
                promoter.promote(
                        workspace,
                        null
                );

        assertFalse(result.isSuccess());
        assertEquals(
                ImprovementLabStatus.VALIDATED,
                workspace.getStatus()
        );
    }

    @Test
    public void promotesValidatedWorkspace() throws Exception {
        Path root =
                Files.createTempDirectory("atlas-promotion-test");

        Path source =
                root.resolve("source");

        Path target =
                root.resolve("target");

        Files.createDirectories(source);
        Files.createDirectories(target);

        Path sourceFile =
                source.resolve("promoted.txt");

        Files.write(
                sourceFile,
                "ATLAS LAB PROMOTED"
                        .getBytes(StandardCharsets.UTF_8)
        );

        ImprovementLabWorkspace workspace =
                new ImprovementLabWorkspace(
                        "lab-success",
                        source.toString(),
                        source.toString(),
                        "snapshot"
                );

        workspace.markReady();
        workspace.markChanged();
        workspace.markValidated();

        ImprovementLabPromoter promoter =
                new ImprovementLabPromoter();

        ImprovementLabPromotionResult result =
                promoter.promote(
                        workspace,
                        target.toString()
                );

        assertTrue(result.isSuccess());
        assertEquals(
                ImprovementLabStatus.PROMOTED,
                workspace.getStatus()
        );

        Path promotedFile =
                target.resolve("promoted.txt");

        assertTrue(Files.exists(promotedFile));

        String content =
                new String(
                        Files.readAllBytes(promotedFile),
                        StandardCharsets.UTF_8
                );

        assertEquals(
                "ATLAS LAB PROMOTED",
                content
        );
    }
}
