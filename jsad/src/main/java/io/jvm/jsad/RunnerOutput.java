package io.jvm.jsad;

public class RunnerOutput {
    public final int code;
    public final byte[] output;
    public final byte[] error;

    public RunnerOutput(
            final int code,
            final byte[] output,
            final byte[] error) {
        this.code = code;
        this.output = output;
        this.error = error;
    }
}
