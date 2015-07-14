package io.jvm.jsad;

import java.io.File;
import java.io.IOException;

public interface Runner {
    public Runner doProxyInput();
    public Runner doProxyOutput();
    public Runner doProxyError();

    public boolean isProxyInput();
    public boolean isProxyOutput();
    public boolean isProxyError();

    public Runner setProxyInput(final boolean proxyInput);
    public Runner setProxyOutput(final boolean proxyOutput);
    public Runner setProxyError(final boolean proxyError);

    // -------------------------------------------------------------------------

    public Output exec(
            final String[] params) throws IOException;

    public Output exec(
            final String[] params,
            final byte[] input) throws IOException;

    public Output exec(
            final String[] params,
            final File workingDir) throws IOException;

    public Output exec(
            final String[] params,
            final byte[] input,
            final File workingDir) throws IOException;

    // -------------------------------------------------------------------------

    public static class Output {
        public final int code;
        public final byte[] output;
        public final byte[] error;

        public Output(
                final int code,
                final byte[] output,
                final byte[] error) {
            this.code = code;
            this.output = output;
            this.error = error;
        }
    }
}
