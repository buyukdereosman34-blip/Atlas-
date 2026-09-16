package com.atlas.app.ai;

public enum LocalAIModel {

    LLAMA("llama"),
    QWEN("qwen"),
    GEMMA("gemma");

    private final String modelFamily;

    LocalAIModel(String modelFamily) {
        this.modelFamily = modelFamily;
    }

    public String getModelFamily() {
        return modelFamily;
    }

    public boolean matches(String modelName) {
        if (modelName == null) {
            return false;
        }

        return modelName
                .trim()
                .toLowerCase()
                .startsWith(modelFamily);
    }

    public static LocalAIModel fromModelName(String modelName) {
        if (modelName != null) {
            String normalized = modelName.trim().toLowerCase();

            for (LocalAIModel model : values()) {
                if (normalized.startsWith(model.modelFamily)) {
                    return model;
                }
            }
        }

        return null;
    }
}
