package io.jvm.jsad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;

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

        final ArrayDeque<String> paramList = new ArrayDeque<String>();
        for(final String prefix: proxyPrefix) paramList.add(prefix);
        for(final String param: params) paramList.add(param);

        final RandomTag tag = new RandomTag();
        final File inputFile, outputFile, errorFile;

        if (proxyInput && input != null) {
            inputFile = new File(workingDir, tag.toString());
            write(inputFile, input);
            paramList.add("<");
            paramList.add(inputFile.getAbsolutePath());
        }
        else {
            inputFile = null;
        }

        if (proxyOutput) {
            outputFile = new File(workingDir, tag.toString());
            paramList.add(">");
            paramList.add(outputFile.getAbsolutePath());
        }
        else {
            outputFile = null;
        }

        if (proxyError) {
            errorFile = new File(workingDir, tag.toString());
            paramList.add("2>");
            paramList.add(errorFile.getAbsolutePath());
        }
        else {
            errorFile = null;
        }

        final String[] newParams = paramList.toArray(new String[paramList.size()]);
        final byte[] newInput = proxyInput ? null : input;

        if (inputFile != null) inputFile.delete();

        final Output output = runnerDirect.exec(newParams, newInput, workingDir);
        if (outputFile == null && errorFile == null) {
            return output;
        }

        final Output newOutput = new Output(
              output.code,
              outputFile == null ? slurp(outputFile) : output.output,
              errorFile == null ? slurp(errorFile) : output.error);

        return newOutput;
    }

// -----------------------------------------------------------------------------

    private static boolean isWindows =
            "\\".equals(System.getProperty("file.separator"));

    private static String[] proxyPrefix = isWindows
            ? new String[] { "cmd", "/c" }
            : new String[] { "/bin/sh" };

// -----------------------------------------------------------------------------

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
