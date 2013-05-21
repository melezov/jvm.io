package io.jvm.jsad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

class RunnerReader implements Callable<byte[]> {
    private final InputStream is;

    public RunnerReader(final InputStream is) {
        this.is = is;
    }

    @Override
    public byte[] call() throws IOException {
        try { Thread.sleep(1000); } catch(Exception e) {}
        System.out.println("Starting call" + is);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final byte[] buffer = new byte[8192];
        while(true) {
            final int read = is.read(buffer);
            if (read == -1) break;

            System.out.println("READ " + read + " BYTES FORM " + is);

            baos.write(buffer, 0, read);
        }

        System.out.println("Ending call" + is);
        return baos.toByteArray();
    }
}
