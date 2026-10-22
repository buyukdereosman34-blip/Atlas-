package com.atlas.app.improvement;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImprovementStuckDetectorTest {

    private final ImprovementStuckDetector detector =
            new ImprovementStuckDetector();

    private ImprovementFailureSignature signature(
            ImprovementFailureCategory category
    ) {
        ImprovementFailureClassification classification =
                new ImprovementFailureClassification(
                        category,
                        "test failure",
                        ""
                );

        return ImprovementFailureSignature.from(classification);
    }

    @Test
    public void codeFailureAllowsThreeAttempts() {
        ImprovementFailureSignature sig = signature(
                ImprovementFailureCategory.CODE
        );

        // Attempt 1: not stuck
        assertFalse(detector.isStuck(
                null,
                sig,
                1,
                ImprovementFailureCategory.CODE
        ));

        // Attempt 2: still not stuck (AI gets retry)
        assertFalse(detector.isStuck(
                sig,
                sig,
                2,
                ImprovementFailureCategory.CODE
        ));

        // Attempt 3: STUCK (limit reached)
        assertTrue(detector.isStuck(
                sig,
                sig,
                3,
                ImprovementFailureCategory.CODE
        ));
    }

    @Test
    public void environmentFailureStuckAtTwoAttempts() {
        ImprovementFailureSignature sig = signature(
                ImprovementFailureCategory.ENVIRONMENT
        );

        // Attempt 1: not stuck
        assertFalse(detector.isStuck(
                null,
                sig,
                1,
                ImprovementFailureCategory.ENVIRONMENT
        ));

        // Attempt 2: STUCK (limit is 2, not 3)
        assertTrue(detector.isStuck(
                sig,
                sig,
                2,
                ImprovementFailureCategory.ENVIRONMENT
        ));
    }

    @Test
    public void differentSignatureIsNeverStuck() {
        ImprovementFailureSignature codeSig = signature(
                ImprovementFailureCategory.CODE
        );
        ImprovementFailureSignature envSig = signature(
                ImprovementFailureCategory.ENVIRONMENT
        );

        assertFalse(detector.isStuck(
                codeSig,
                envSig,
                5,
                ImprovementFailureCategory.CODE
        ));
    }

    @Test
    public void nullSignaturesReturnFalse() {
        ImprovementFailureSignature sig = signature(
                ImprovementFailureCategory.CODE
        );

        assertFalse(detector.isStuck(
                null,
                null,
                10,
                ImprovementFailureCategory.CODE
        ));

        assertFalse(detector.isStuck(
                sig,
                null,
                10,
                ImprovementFailureCategory.CODE
        ));
    }

    @Test
    public void toolchainFailureStuckAtTwoAttempts() {
        ImprovementFailureSignature sig = signature(
                ImprovementFailureCategory.TOOLCHAIN
        );

        assertFalse(detector.isStuck(
                sig,
                sig,
                1,
                ImprovementFailureCategory.TOOLCHAIN
        ));

        assertTrue(detector.isStuck(
                sig,
                sig,
                2,
                ImprovementFailureCategory.TOOLCHAIN
        ));
    }

    @Test
    public void backwardCompatibleOverloadStillWorks() {
        ImprovementFailureSignature sig = signature(
                ImprovementFailureCategory.UNKNOWN
        );

        // Legacy overload: isStuck(prev, curr) uses
        // count=2, category=UNKNOWN -> limit=2 -> returns true.
        assertTrue(detector.isStuck(sig, sig));
    }
}
