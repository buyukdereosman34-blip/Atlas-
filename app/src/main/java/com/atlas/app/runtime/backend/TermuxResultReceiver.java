package com.atlas.app.runtime.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Receives RUN_COMMAND results from Termux and exposes the latest
 * result via a static holder (polling model; caller reads lastResult()).
 * <p>
 * Extra keys logged on first run so we can verify Phase B wiring.
 */
public final class TermuxResultReceiver extends BroadcastReceiver {

    private static final String TAG = "TermuxResultReceiver";

    public static final String ACTION_RESULT = "com.atlas.app.TERMUX_RESULT";
    public static final String EXTRA_OP = "op";

    private static final String BUNDLE_KEY_1 = "com.termux.RUN_COMMAND_RESULT_BUNDLE_KEY";
    private static final String BUNDLE_KEY_2 = "com.termux.RUN_COMMAND_RESULT_BUNDLE";

    public static final class LastResult {
        public final String op;
        public final int exitCode;
        public final String stdout;
        public final String stderr;
        public final long timestamp;

        LastResult(String op, int exitCode, String stdout, String stderr) {
            this.op = op;
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
            this.timestamp = System.currentTimeMillis();
        }

        @Override public String toString() {
            return "LastResult{op=" + op + ", exit=" + exitCode
                    + ", stdout=" + (stdout == null ? "" : stdout.length()) + "B"
                    + ", stderr=" + (stderr == null ? "" : stderr.length()) + "B}";
        }
    }

    private static volatile LastResult lastResult;

    public static LastResult lastResult() {
        return lastResult;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String op = intent.getStringExtra(EXTRA_OP);

        Bundle all = intent.getExtras();
        if (all != null) {
            for (String k : all.keySet()) {
                Object v = all.get(k);
                String vs = String.valueOf(v);
                if (vs.length() > 200) vs = vs.substring(0, 200) + "...";
                Log.d(TAG, "extra[" + k + "]=" + vs);
            }
        }

        Bundle bundle = intent.getBundleExtra(BUNDLE_KEY_1);
        if (bundle == null) bundle = intent.getBundleExtra(BUNDLE_KEY_2);

        int exit = bundle != null ? bundle.getInt("exitCode", -1) : -1;
        String stdout = bundle != null ? bundle.getString("stdout", "") : "";
        String stderr = bundle != null ? bundle.getString("stderr", "") : "";

        lastResult = new LastResult(op, exit, stdout, stderr);
        Log.i(TAG, "Result " + lastResult);
    }
}
