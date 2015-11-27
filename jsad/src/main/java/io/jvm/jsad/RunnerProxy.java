package io.jvm.jsad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

final class RunnerProxy extends RunnerBase {
    private final RunnerDirect runnerDirect;
    private final boolean proxyInput;
    private final boolean proxyOutput;
    private final boolean proxyError;

    RunnerProxy(
            final RunnerDirect runnerDirect,
            final boolean proxyInput,
            final boolean proxyOutput,
            final boolean proxyError) {
        this.runnerDirect = runnerDirect;
        this.proxyInput = proxyInput;
        this.proxyOutput = proxyOutput;
        this.proxyError = proxyError;
    }

    // -------------------------------------------------------------------------

    @Override
    public boolean isProxyInput() {
        return proxyInput;
    }

    @Override
    public boolean isProxyOutput() {
        return proxyOutput;
    }

    @Override
    public boolean isProxyError() {
        return proxyError;
    }

    @Override
    public Runner setProxyInput(final boolean proxyInput) {
        if (proxyInput == this.proxyInput) return this;
        if (!proxyInput && !proxyOutput && !proxyError) return runnerDirect;
        return new RunnerProxy(runnerDirect, proxyInput, proxyOutput, proxyError);
    }

    @Override
    public Runner setProxyOutput(final boolean proxyOutput) {
        if (proxyOutput == this.proxyOutput) return this;
        if (!proxyInput && !proxyOutput && !proxyError) return runnerDirect;
        return new RunnerProxy(runnerDirect, proxyInput, proxyOutput, proxyError);
    }

    @Override
    public Runner setProxyError(final boolean proxyError) {
        if (proxyError == this.proxyError) return this;
        if (!proxyInput && !proxyOutput && !proxyError) return runnerDirect;
        return new RunnerProxy(runnerDirect, proxyInput, proxyOutput, proxyError);
    }

    // -------------------------------------------------------------------------

    @Override
    public Output exec(
            final String[] params,
            final byte[] input,
            final File workingDir) throws IOException {

        if (!(proxyInput || proxyOutput || proxyError)) {
            // If explicitly instantiated with all proxies turned off.
            return runnerDirect.exec(params, input, workingDir);
        }

        final File absPath = workingDir.getAbsoluteFile();
        final OsProxy osProxy = OsProxy.getOsProxy();
        final StringBuilder proxyBody = new StringBuilder(osProxy.header);

        final String processTag =
            String.format("%013d-%08X", System.currentTimeMillis(), random.nextInt());

        final File inputFile, outputFile, errorFile;

        if (proxyInput && input != null) {
            inputFile = new File(absPath, processTag + ".input");
            dump(inputFile, input);
            proxyBody.append(" < \"").append(inputFile.getPath()).append('"');
        } else {
            inputFile = null;
        }

        if (proxyOutput) {
            outputFile = new File(absPath, processTag + ".output");
            proxyBody.append(" > \"").append(outputFile.getPath()).append('"');
        } else {
            outputFile = null;
        }

        if (proxyError) {
            errorFile = new File(absPath, processTag + ".error");
            proxyBody.append(" 2> \"").append(errorFile.getPath()).append('"');
        } else {
            errorFile = null;
        }

        final File proxyFile = new File(absPath, processTag + osProxy.extension);
        dump(proxyFile, proxyBody.toString().getBytes(osProxy.encoding));
        proxyFile.setExecutable(true,true);
        final String[] newParams = new String[osProxy.shell.length + params.length + 1];
        newParams[0] = proxyFile.getAbsolutePath();
        System.arraycopy(osProxy.shell, 0, newParams, 1, osProxy.shell.length);
        System.arraycopy(params, 0, newParams, 1 + osProxy.shell.length, params.length);

        final byte[] newInput = proxyInput ? null : input;

        final Output output = runnerDirect.exec(newParams, newInput, workingDir);
        proxyFile.delete();
        if (inputFile != null) inputFile.delete();

        if (outputFile == null && errorFile == null) {
            return output;
        }

        final Output newOutput = new Output(
              output.code,
              outputFile != null ? slurp(outputFile) : output.output,
              errorFile != null ? slurp(errorFile) : output.error);

        return newOutput;
    }

    // -------------------------------------------------------------------------

    private static enum OsProxy {
        WINDOWS(new String[] { "cmd", "/c" }, "@echo off\r\n%*", "cp1250", ".bat"),
        LINUX  (new String[] { "/bin/sh" },   "#!/bin/sh\n$@",   "UTF-8",  ".sh" );

        public final String[] shell;
        public final String header;
        public final String encoding;
        public final String extension;

        private OsProxy(
                final String[] shell,
                final String header,
                final String encoding,
                final String extension) {
            this.shell = shell;
            this.header = header;
            this.encoding = encoding;
            this.extension = extension;
        }

        private static boolean isWindows =
                System.getProperty("os.name")
                    .toLowerCase(Locale.ENGLISH)
                    .contains("windows");

        public static OsProxy getOsProxy() {
            return isWindows ? WINDOWS : LINUX;
        }
    }

    // -------------------------------------------------------------------------

    private static Random random = new Random();

    /** Writes the byte array into a file */
    private static void dump(final File file, final byte[] body) throws IOException {
        final FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(body);
        } finally {
            fos.close();
        }
    }

    /** Reads the file into a byte array and deletes the file */
    private static byte[] slurp(final File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        try {
            final byte[] body = new byte[(int) file.length()];
            fis.read(body);
            return body;
        } finally {
            fis.close();
            file.delete();
        }
    }
}
