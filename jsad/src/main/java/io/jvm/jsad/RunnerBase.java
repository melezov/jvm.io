package io.jvm.jsad;

import java.io.IOException;

public abstract class RunnerBase implements Runner {
    @Override
    public Runner doProxyInput() {
        return setProxyInput(true);
    }

    public Runner doProxyOutput() {
        return setProxyOutput(true);
    }

    public Runner doProxyError() {
        return setProxyError(true);
    }

    @Override
    public Output exec(
            final String[] params) throws IOException {
        return exec(params, null, null);
    }

    @Override
    public Output exec(
            final String[] params,
            final byte[] input) throws IOException {
        return exec(params, input, null);
    }
}
