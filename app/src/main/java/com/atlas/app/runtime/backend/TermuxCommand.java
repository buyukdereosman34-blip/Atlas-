package com.atlas.app.runtime.backend;

/**
 * Hard-coded Termux command templates.
 * <p>
 * SECURITY: No string interpolation from user/tool input.
 * Every op is a compile-time constant. Wired to real scripts in Phase B.
 */
public enum TermuxCommand {
    CREATE ("echo create-ok"),
    APPLY  ("echo apply-ok"),
    BUILD  ("echo build-ok"),
    TEST   ("echo test-ok"),
    HASH   ("echo hash-ok"),
    PROMOTE("echo promote-ok");

    private final String script;
    TermuxCommand(String script) { this.script = script; }
    public String script() { return script; }
}
