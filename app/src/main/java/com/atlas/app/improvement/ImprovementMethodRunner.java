package com.atlas.app.improvement;

import java.util.List;

public class ImprovementMethodRunner {

    private final ImprovementMethodRanker ranker;

    public ImprovementMethodRunner(
            ImprovementMethodRanker ranker
    ) {
        this.ranker = ranker;
    }

    public ImprovementMethod run(
            List<ImprovementMethod> methods,
            ImprovementMethodExecutor executor
    ) {
        if (methods == null || methods.isEmpty()) {
            return null;
        }

        if (executor == null) {
            return null;
        }

        List<ImprovementMethod> ranked =
                ranker.rank(methods);

        for (ImprovementMethod method : ranked) {
            boolean success = executor.execute(method);

            if (success) {
                method.recordSuccess();
                return method;
            }

            method.recordFailure();
        }

        return null;
    }
}
