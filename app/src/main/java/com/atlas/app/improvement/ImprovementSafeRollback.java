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

        File labDir;
        try {
            // PATH TRAVERSAL KORUMASI: Yolu canonical hale getirerek ../ saldirilarini onle
            labDir = new File(labPath).getCanonicalFile();
        } catch (Exception e) {
            return false;
        }

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

            // Claude'un uyarisi: GIT_DIR sizmasini engelle
            pb.environment().remove("GIT_DIR");
            pb.environment().remove("GIT_WORK_TREE");

            Process process = pb.start();
            // Claude'un uyarisi: Okuma yapilmali, yoksa pipe dolabilir
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                while (reader.readLine() != null) { }
            }
            
            // Timeout ekle (30 saniye)
            if (!process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return false;
            }

            return process.exitValue() == 0;

        } catch (Exception e) {
            return false;
        }
    }
}
