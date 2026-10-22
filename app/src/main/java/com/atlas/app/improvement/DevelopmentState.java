package com.atlas.app.improvement;

/**
 * Self-Development onay ve yürütme durumları.
 * ChatGPT'nin 10 durumlu önerisi.
 *
 * Akış:
 *   PROPOSED -> PENDING_APPROVAL -> APPROVED
 *   -> APPLYING -> VALIDATING
 *   -> PROMOTED (başarı)
 *   -> ROLLBACK_REQUIRED -> ROLLED_BACK (başarısız)
 *   -> REJECTED (kullanıcı reddetti)
 *   -> FAILED (diğer)
 */
public enum DevelopmentState {

    PROPOSED,
    PENDING_APPROVAL,
    APPROVED,
    APPLYING,
    VALIDATING,
    PROMOTED,
    ROLLBACK_REQUIRED,
    ROLLED_BACK,
    REJECTED,
    FAILED;

    public boolean isTerminal() {
        return this == PROMOTED
                || this == ROLLED_BACK
                || this == REJECTED
                || this == FAILED;
    }

    public boolean isInProgress() {
        return this == APPROVED
                || this == APPLYING
                || this == VALIDATING;
    }
}
