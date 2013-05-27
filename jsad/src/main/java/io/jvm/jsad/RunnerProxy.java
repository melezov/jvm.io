package io.jvm.jsad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class RunnerProxy extends RunnerBase {
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

// -----------------------------------------------------------------------------

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
        return new RunnerProxy(runnerDirect, proxyInput, proxyOutput, proxyError);
    }

    @Override
    public Runner setProxyOutput(final boolean proxyOutput) {
        return new RunnerProxy(runnerDirect, proxyInput, proxyOutput, proxyError);
    }

    @Override
    public Runner setProxyError(final boolean proxyError) {
        return new RunnerProxy(runnerDirect, proxyInput, proxyOutput, proxyError);
    }

 // -----------------------------------------------------------------------------

    @Override
    public Output exec(
            final String[] params,
            final byte[] input,
            final File workingDir) throws IOException {

        if (!(proxyInput || proxyOutput || proxyError)) {
            return runnerDirect.exec(params, input, workingDir);
        }

        final OsProxy osProxy = OsProxy.getOsProxy();
        final StringBuilder proxyBody = new StringBuilder(osProxy.header);

        final String processTag =
            String.format("%016X-%016X", System.currentTimeMillis(), random.nextLong());

        final File inputFile, outputFile, errorFile;

        if (proxyInput && input != null) {
            inputFile = new File(workingDir, processTag + ".input");
            write(inputFile, input);
            proxyBody.append(" < \"").append(inputFile.getAbsolutePath()).append('"');
        }
        else {
            inputFile = null;
        }

        if (proxyOutput) {
            outputFile = new File(workingDir, processTag + ".output");
            proxyBody.append(" > \"").append(outputFile.getAbsolutePath()).append('"');
        }
        else {
            outputFile = null;
        }

        if (proxyError) {
            errorFile = new File(workingDir, processTag + ".error");
            proxyBody.append(" 2> \"").append(errorFile.getAbsolutePath()).append('"');
        }
        else {
            errorFile = null;
        }

        final File proxyFile = new File(workingDir, processTag + osProxy.extension);
        write(proxyFile, proxyBody.toString().getBytes(osProxy.encoding));

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

// -----------------------------------------------------------------------------

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
            "\\".equals(System.getProperty("file.separator"));

        public static OsProxy getOsProxy() {
            return isWindows ? WINDOWS : LINUX;
        }
    }

// -----------------------------------------------------------------------------

    private static Random random = new Random();

    private static void write(final File file, final byte[] body) throws IOException {
        final FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(body);
        }
        finally {
            fos.close();
        }
    }

    private static byte[] slurp(final File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        try {
            final byte[] body = new byte[(int) file.length()];
            fis.read(body);
            return body;
        }
        finally {
            fis.close();
            file.delete();
        }
    }
}
