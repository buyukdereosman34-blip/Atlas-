package com.atlas.app.improvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImprovementMethodStore {

    private final List<ImprovementMethod> methods =
            new ArrayList<>();

    public void add(ImprovementMethod method) {
        if (method == null) {
            return;
        }

        methods.add(method);
    }

    public List<ImprovementMethod> getAll() {
        return Collections.unmodifiableList(methods);
    }

    public ImprovementMethod findBest() {
        ImprovementMethod best = null;

        for (ImprovementMethod method : methods) {
            if (best == null || method.getScore() > best.getScore()) {
                best = method;
            }
        }

        return best;
    }

    public int size() {
        return methods.size();
    }
}
