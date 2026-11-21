package com.atlas.app.runtime;

import java.util.concurrent.CopyOnWriteArrayList;

public final class CapabilityRegistry {
    private final CopyOnWriteArrayList<CapabilityBackend> backends = new CopyOnWriteArrayList<>();

    public void register(CapabilityBackend backend) {
        if (backend == null || backend.name() == null) return;
        backends.add(backend);
    }

    public void unregister(String name) {
        backends.removeIf(b -> b.name().equals(name));
    }

    public CopyOnWriteArrayList<CapabilityBackend> all() { return backends; }

    public boolean hasHealthy(CapabilityBackend.Capability cap) {
        for (CapabilityBackend b : backends) {
            if (b.capabilities().contains(cap) && b.health() != CapabilityBackend.Health.DOWN) {
                return true;
            }
        }
        return false;
    }
}
