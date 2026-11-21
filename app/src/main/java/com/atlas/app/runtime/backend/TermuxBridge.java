package com.atlas.app.runtime.backend;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Sends RUN_COMMAND intents to Termux.
 * <p>
 * Requirements:
 * - Termux installed with com.termux.api
 * - ~/.termux/termux.properties: allow-external-apps=true
 * - Manifest: uses-permission com.termux.permission.RUN_COMMAND + <queries>
 * <p>
 * Security: only TermuxCommand enum values accepted; no free-form strings.
 */
public final class TermuxBridge {

    private static final String TAG = "TermuxBridge";

    public static final String TERMUX_PKG = "com.termux";
    public static final String RUN_COMMAND_SERVICE = "com.termux.app.RunCommandService";
    public static final String ACTION_RUN_COMMAND = "com.termux.RUN_COMMAND";

    public static final String EXTRA_COMMAND_PATH   = "com.termux.RUN_COMMAND_PATH";
    public static final String EXTRA_ARGUMENTS      = "com.termux.RUN_COMMAND_ARGUMENTS";
    public static final String EXTRA_WORKDIR        = "com.termux.RUN_COMMAND_WORKDIR";
    public static final String EXTRA_BACKGROUND     = "com.termux.RUN_COMMAND_BACKGROUND";
    public static final String EXTRA_SESSION_ACTION = "com.termux.RUN_COMMAND_SESSION_ACTION";
    public static final String EXTRA_PENDING_INTENT = "com.termux.RUN_COMMAND_PENDING_INTENT";
    public static final String EXTRA_COMMAND_LABEL  = "com.termux.RUN_COMMAND_COMMAND_LABEL";

    public static final String TERMUX_BIN  = "/data/data/com.termux/files/usr/bin/bash";
    public static final String TERMUX_HOME = "/data/data/com.termux/files/home";

    private final Context context;

    public TermuxBridge(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Fire-and-forget: dispatch the op to Termux.
     * Result arrives at TermuxResultReceiver via broadcast.
     */
    public boolean dispatch(TermuxCommand op) {
        if (op == null) {
            Log.w(TAG, "dispatch: null op");
            return false;
        }
        try {
            Intent intent = new Intent();
            intent.setClassName(TERMUX_PKG, RUN_COMMAND_SERVICE);
            intent.setAction(ACTION_RUN_COMMAND);

            intent.putExtra(EXTRA_COMMAND_PATH, TERMUX_BIN);
            intent.putExtra(EXTRA_ARGUMENTS, new String[]{"-lc", op.script()});
            intent.putExtra(EXTRA_WORKDIR, TERMUX_HOME);
            intent.putExtra(EXTRA_BACKGROUND, true);
            intent.putExtra(EXTRA_SESSION_ACTION, "0");
            intent.putExtra(EXTRA_COMMAND_LABEL, "ATLAS:" + op.name());

            Intent resultIntent = new Intent(TermuxResultReceiver.ACTION_RESULT);
            resultIntent.setPackage(context.getPackageName());
            resultIntent.putExtra(TermuxResultReceiver.EXTRA_OP, op.name());

            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    op.ordinal(),
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            intent.putExtra(EXTRA_PENDING_INTENT, pi);

            context.startService(intent);
            Log.i(TAG, "Dispatched: " + op.name());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "dispatch failed for " + op.name(), e);
            return false;
        }
    }
}
