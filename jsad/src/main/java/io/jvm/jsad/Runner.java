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

public class Runner {
    private final ExecutorService executor;

    public Runner() {
        executor = Executors.newCachedThreadPool();
    }

    public Output exec(
            final String[] params,
            final byte[] input,
            final File workingDir) throws IOException {

        final ProcessBuilder pb = new ProcessBuilder(params);
        pb.directory(workingDir);
        pb.command(params);

        try {
            final Process process = pb.start();

            final Future<byte[]> out = executor.submit(new Consumer(process.getInputStream()));
            final Future<byte[]> err = executor.submit(new Consumer(process.getErrorStream()));

            final OutputStream in = process.getOutputStream();
            if (input != null) in.write(input);
            in.close();

            return new Output(
                    process.waitFor(),
                    out.get(),
                    err.get());
        }
        catch (final Throwable t) {
            throw new IOException("Error while running process", t);
        }
    }

    @Override
    public void finalize() {
        executor.shutdown();
    }

 // -----------------------------------------------------------------------------

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

// -----------------------------------------------------------------------------

    private static class Consumer implements Callable<byte[]> {
        private final InputStream is;

        public Consumer(final InputStream is) {
            this.is = is;
        }

        @Override
        public byte[] call() throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final byte[] buffer = new byte[8192];
            while(true) {
                final int read = is.read(buffer);
                if (read == -1) break;
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();
        }
    }

}
