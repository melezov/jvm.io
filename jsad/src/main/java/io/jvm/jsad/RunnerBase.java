package io.jvm.jsad;

import java.io.File;
import java.io.IOException;

abstract class RunnerBase implements Runner {
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
            final File workingDir) throws IOException {
        return exec(params, null, workingDir);
    }

    @Override
    public Output exec(
            final String[] params,
            final byte[] input) throws IOException {
        return exec(params, input, null);
    }
}
