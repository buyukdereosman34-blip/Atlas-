package com.atlas.app.shizuku;

import android.content.pm.PackageManager;

import rikka.shizuku.Shizuku;

public class ShizukuManager {

    public interface Listener {
        void onStatusChanged(Status status);
    }

    public enum Status {
        NOT_RUNNING,
        PERMISSION_REQUIRED,
        PERMISSION_GRANTED
    }

    private static final int REQUEST_CODE = 1001;

    private final Listener listener;

    public ShizukuManager(Listener listener) {
        this.listener = listener;
    }

    public Status getStatus() {
        if (!Shizuku.pingBinder()) {
            return Status.NOT_RUNNING;
        }

        if (Shizuku.checkSelfPermission()
                == PackageManager.PERMISSION_GRANTED) {
            return Status.PERMISSION_GRANTED;
        }

        return Status.PERMISSION_REQUIRED;
    }

    public void check() {
        if (listener != null) {
            listener.onStatusChanged(getStatus());
        }
    }

    public void requestPermission() {
        if (!Shizuku.pingBinder()) {
            check();
            return;
        }

        if (Shizuku.checkSelfPermission()
                == PackageManager.PERMISSION_GRANTED) {
            check();
            return;
        }

        Shizuku.requestPermission(REQUEST_CODE);
    }

    public void handlePermissionResult(int requestCode, int grantResult) {
        if (requestCode != REQUEST_CODE) {
            return;
        }

        check();
    }
}
