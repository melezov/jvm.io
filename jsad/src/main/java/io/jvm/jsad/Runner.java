package io.jvm.jsad;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Runner {
    public final File workingDir;
    public final int waitPeriod;

    public Runner(
            final File workingDir,
            final int waitPeriod) {
        this.workingDir = workingDir;
        this.waitPeriod = waitPeriod;
    }

    public Runner() {
        this(new File("."), 0);
    }

    private static ExecutorService executor =
        Executors.newCachedThreadPool();

    public RunnerOutput exec(
            final String[] params) throws IOException {
        return exec(params, null);
    }

    public RunnerOutput exec(
            final String[] params,
            final byte[] input) throws IOException {

        final ProcessBuilder pb = new ProcessBuilder(params);
        pb.directory(workingDir);
        pb.command(params);

        try {
            final Process process = pb.start();

            final Future<byte[]> out = executor.submit(new RunnerReader(process.getInputStream()));
            final Future<byte[]> err = executor.submit(new RunnerReader(process.getErrorStream()));

            process.getOutputStream().write(input);

            return new RunnerOutput(
                    process.waitFor(),
                    out.get(),
                    err.get());
        }
        catch (final Throwable t) {
            throw new IOException("Could not execute process", t);
        }
    }
}
