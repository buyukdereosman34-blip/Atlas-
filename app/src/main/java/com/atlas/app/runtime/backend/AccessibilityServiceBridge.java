package com.atlas.app.runtime.backend;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public final class AccessibilityServiceBridge extends AccessibilityService {

    private static volatile AccessibilityServiceBridge instance;

    public static AccessibilityServiceBridge get() { return instance; }
    public AccessibilityBackend asBackend() { return new AccessibilityBackend(this); }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override public void onInterrupt() { }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }
}
