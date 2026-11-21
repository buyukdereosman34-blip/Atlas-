package com.atlas.app.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * OmniRoute: Birden çok sağlayıcıyı sırayla dener, ilk başarılı yanıtı döner.
 * Sağlayıcılar öncelik sırasına göre çağrılır.
 */
public final class OmniRouteProvider implements AIProvider {

    private final List<AIProvider> chain = new ArrayList<>();

    public OmniRouteProvider() {}

    public void add(AIProvider provider) {
        if (provider != null && !(provider instanceof OmniRouteProvider)) {
            chain.add(provider);
        }
    }

    public int size() { return chain.size(); }

    @Override public String getName() { return "omniroute"; }
    @Override public AIProviderType getType() { return AIProviderType.HYBRID; }

    @Override
    public boolean isAvailable() {
        for (AIProvider p : chain) {
            if (p.isAvailable()) return true;
        }
        return false;
    }

    @Override
    public AIResponse generate(AIRequest request) {
        if (request == null) {
            return AIResponse.failure("OmniRoute request is null", -100);
        }
        StringBuilder errors = new StringBuilder();
        for (AIProvider p : chain) {
            if (!p.isAvailable()) continue;
            try {
                AIResponse r = p.generate(request);
                if (r != null && r.isSuccess() && !r.getOutput().trim().isEmpty()) {
                    return r;
                }
                errors.append(p.getName()).append(": ")
                      .append(r == null ? "null" : r.getOutput()).append("; ");
            } catch (Exception e) {
                errors.append(p.getName()).append(": ").append(e.getMessage()).append("; ");
            }
        }
        return AIResponse.failure("OmniRoute all failed: " + errors, -101);
    }
}
