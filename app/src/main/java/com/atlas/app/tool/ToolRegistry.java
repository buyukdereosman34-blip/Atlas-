package com.atlas.app.tool;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolRegistry {

    private final Map<String, AtlasTool> tools =
            new LinkedHashMap<>();

    public void register(AtlasTool tool) {
        if (tool == null) {
            return;
        }

        String name = tool.getName();

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        tools.put(name.trim().toLowerCase(), tool);
    }

    public AtlasTool get(String name) {
        if (name == null) {
            return null;
        }

        return tools.get(name.trim().toLowerCase());
    }

    public Collection<AtlasTool> getAll() {
        return tools.values();
    }

    public boolean contains(String name) {
        return get(name) != null;
    }

    public int size() {
        return tools.size();
    }
}
