package com.atlas.app.ai;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Live test — requires ATLAS_AI_GROQ_API_KEY env var.
 * Skips (fails gracefully) if key not present.
 */
public class GroqLiveTest {

    @Test
    public void groqResponds() {
        String key = System.getenv("ATLAS_AI_GROQ_API_KEY");

        if (key == null || key.trim().isEmpty()) {
            System.out.println("[SKIP] Groq key yok");
            return;
        }

        System.out.println("[INFO] Groq key var, test ediliyor...");

        AIProviderRegistry registry =
                AIProviderBootstrap.createRegistry();

        AIProvider groq = registry.get("groq");
        assertNotNull("Groq provider kayitli olmali", groq);
        System.out.println("[INFO] Groq available: " + groq.isAvailable());

        // Debug: endpoint ve model
        if (groq instanceof CloudAIProviderBase) {
            // endpoint goremiyoruz, ama key ve config'i gorebiliriz
            System.out.println("[INFO] Groq class: " + groq.getClass().getName());
        }

        if (!groq.isAvailable()) {
            System.out.println("[SKIP] Groq available degil");
            return;
        }

        AIResponse response = groq.generate(
                new AIRequest(
                        "Sen kisa cevap veren bir asistansin.",
                        "Sadece 'Merhaba ATLAS' yaz."
                )
        );

        System.out.println("[INFO] Groq cevap: " + response.getOutput());
        System.out.println("[INFO] Success: " + response.isSuccess());

        assertTrue("Groq cevap vermeli", response.isSuccess());
        assertNotNull(response.getOutput());
    }
}
