package io.jvm.jsad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class RunnerDirect extends RunnerBase {
    private final ExecutorService executorService;

    public RunnerDirect() {
        executorService = Executors.newCachedThreadPool();
    }

    public RunnerDirect(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    // -------------------------------------------------------------------------

    @Override
    public boolean isProxyInput() {
        return false;
    }

    @Override
    public boolean isProxyOutput() {
        return false;
    }

    @Override
    public boolean isProxyError() {
        return false;
    }

    @Override
    public Runner setProxyInput(final boolean proxyInput) {
        return proxyInput ? new RunnerProxy(this, proxyInput, false, false) : this;
    }

    @Override
    public Runner setProxyOutput(final boolean proxyOutput) {
        return proxyOutput ? new RunnerProxy(this, false, proxyOutput, false) : this;
    }

    @Override
    public Runner setProxyError(final boolean proxyError) {
        return proxyError ? new RunnerProxy(this, false, false, proxyError) : this;
    }

    // -------------------------------------------------------------------------

    @Override
    public Output exec(
            final String[] params,
            final byte[] input,
            final File workingDir) throws IOException {

        final ProcessBuilder pb = new ProcessBuilder(params);
        if (workingDir != null) pb.directory(workingDir);

        try {
            final Process process = pb.start();

            final Future<byte[]> out = executorService.submit(new Consumer(process.getInputStream()));
            final Future<byte[]> err = executorService.submit(new Consumer(process.getErrorStream()));

            final OutputStream in = process.getOutputStream();
            try {
                if (input != null) in.write(input);
            } catch (final IOException e) {
                // Reporting error via byte-to-char encoding
                throw new IOException(new String(err.get(), "ISO-8859-1"), e);
            } finally {
                in.close();
            }

            return new Output(
                    process.waitFor(),
                    out.get(),
                    err.get());
        } catch (final Throwable t) {
            throw new IOException("Error while running process", t);
        }
    }

    @Override
    public void finalize() {
        executorService.shutdown();
    }

    // -------------------------------------------------------------------------

    private static class Consumer implements Callable<byte[]> {
        private final InputStream is;

        public Consumer(final InputStream is) {
            this.is = is;
        }

        @Override
        public byte[] call() throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final byte[] buffer = new byte[8192];
            while (true) {
                final int read = is.read(buffer);
                if (read == -1) break;
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();
        }
    }
}
