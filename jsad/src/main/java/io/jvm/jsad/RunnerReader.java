package io.jvm.jsad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class RunnerReader implements Callable<byte[]> {
    private final InputStream is;

    private RunnerReader(final InputStream is) {
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

    public static byte[] read(final InputStream is) throws IOException {
        final RunnerReader sr = new RunnerReader(is);
        final Future<byte[]> output = new FutureTask<byte[]>(sr);
        try {
            return output.get();
        }
        catch(final Throwable t) {
            throw new IOException("Could not read stream", t);
        }
    }
}
