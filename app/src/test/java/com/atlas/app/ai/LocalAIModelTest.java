package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LocalAIModelTest {

    @Test
    public void detectsLlamaModels() {
        assertTrue(LocalAIModel.LLAMA.matches("llama3.2"));
        assertTrue(LocalAIModel.LLAMA.matches("llama3.1:8b"));
        assertEquals(
                LocalAIModel.LLAMA,
                LocalAIModel.fromModelName("llama3.2")
        );
    }

    @Test
    public void detectsQwenModels() {
        assertTrue(LocalAIModel.QWEN.matches("qwen2.5"));
        assertEquals(
                LocalAIModel.QWEN,
                LocalAIModel.fromModelName("qwen2.5:7b")
        );
    }

    @Test
    public void detectsGemmaModels() {
        assertTrue(LocalAIModel.GEMMA.matches("gemma3"));
        assertEquals(
                LocalAIModel.GEMMA,
                LocalAIModel.fromModelName("gemma3:4b")
        );
    }

    @Test
    public void unknownModelReturnsNull() {
        assertNull(
                LocalAIModel.fromModelName("mistral")
        );
    }
}
