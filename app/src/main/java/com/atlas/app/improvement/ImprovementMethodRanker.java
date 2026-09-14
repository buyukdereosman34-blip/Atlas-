package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImprovementMethodRanker {

    public List<ImprovementMethod> rank(
            List<ImprovementMethod> methods
    ) {
        if (methods == null || methods.isEmpty()) {
            return Collections.emptyList();
        }

        List<ImprovementMethod> ranked =
                new ArrayList<>(methods);

        ranked.sort(
                Comparator.comparingInt(
                        ImprovementMethod::getScore
                ).reversed()
        );

        return Collections.unmodifiableList(ranked);
    }

    public ImprovementMethod best(
            List<ImprovementMethod> methods
    ) {
        List<ImprovementMethod> ranked = rank(methods);

        if (ranked.isEmpty()) {
            return null;
        }

        return ranked.get(0);
    }
}
