package com.atlas.app.improvement;

import java.io.File;

/**
 * LAB ortamında güvenli geri alma.
 *
 * KURAL: git reset --hard YASAK (kullanıcı verisini siler).
 * KURAL: git clean -fd YASAK (stash'i siler).
 *
 * Sadece git stash --include-untracked kullanılır.
 * Snapshot tabanlı geri alma, ImprovementLabManager'da
 * zaten mevcut; ileride oradan çağrılacak.
 */
public final class ImprovementSafeRollback {

    private ImprovementSafeRollback() {
    }

    public static boolean safeRollbackLab(String labPath) {
        if (labPath == null || labPath.trim().isEmpty()) {
            return false;
        }

        File labDir = new File(labPath);

        if (!labDir.exists() || !labDir.isDirectory()) {
            return false;
        }

        File gitDir = new File(labDir, ".git");

        if (!gitDir.exists()) {
            // Git yok; snapshot tabanlı geri alma gerekir.
            // Bu metot sadece git stash yapar.
            return false;
        }

        return executeGitCommand(
                labDir,
                "git",
                "stash",
                "--include-untracked"
        );
    }

    private static boolean executeGitCommand(
            File workingDir,
            String... command
    ) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            int exitCode = process.waitFor();

            return exitCode == 0;

        } catch (Exception e) {
            return false;
        }
    }
}
