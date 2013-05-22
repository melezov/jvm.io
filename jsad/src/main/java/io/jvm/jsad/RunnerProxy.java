package io.jvm.jsad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RunnerProxy {
    private final Runner runner;

    public final File inputFile;
    public final File outputFile;
    public final File errorFile;

    private RunnerProxy(
            final Runner runner,
            final File inputFile,
            final File outputFile,
            final File errorFile) {
        this.runner = runner;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.errorFile = errorFile;
    }

    public RunnerProxy(final Runner runner) {
        this(runner, null, null, null);
    }

    public RunnerProxy withProxyInput(final File inputFile) {
        return new RunnerProxy(runner, inputFile, outputFile, errorFile);
    }

    public RunnerProxy withProxyInput(final byte[] input, final File workingDir) {
        return new RunnerProxy(runner, inputFile, outputFile, errorFile);
    }

    public RunnerProxy withProxyOutput(final File outputFile) {
        return new RunnerProxy(runner, inputFile, outputFile, errorFile);
    }

    public RunnerProxy withProxyError(final File errorFile) {
        return new RunnerProxy(runner, inputFile, outputFile, errorFile);
    }

    public Runner.Output exec(
            final String[] params,
            final byte[] input,
            final File workingDir) throws IOException {

        final List<String> paramList = Arrays.asList(proxyPrefix);
        for(final String param: params) paramList.add(param);

        if (inputFile != null) {
            paramList.add("<");
            paramList.add(inputFile.getAbsolutePath());
        }

        if (outputFile != null) {
            paramList.add(">");
            paramList.add(outputFile.getAbsolutePath());
        }

        if (errorFile != null) {
            paramList.add("2>");
            paramList.add(errorFile.getAbsolutePath());
        }

        final String[] newParams = paramList.toArray(new String[paramList.size()]);
        final byte[] newInput = inputFile != null ? null : input;

        final Runner.Output output = runner.exec(newParams, newInput, workingDir);
        if (outputFile == null && errorFile == null) {
            return output;
        }

        return new Runner.Output(
                output.code,
                outputFile == null ? slurpFile(outputFile) : output.output,
                errorFile == null ? slurpFile(errorFile) : output.error);
    }

// -----------------------------------------------------------------------------

    private static boolean isWindows =
        "\\".equals(System.getProperty("file.separator"));

    private static String[] proxyPrefix = isWindows
        ? new String[] { "cmd", "/c" }
        : new String[] { "/bin/sh" };

// -----------------------------------------------------------------------------

    private static void writeFile(final File file, final byte[] body) throws IOException {
        final FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(body);
        }
        finally {
            fos.close();
        }
    }

    private static byte[] readFile(final File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        try {
            final byte[] body = new byte[(int) file.length()];
            fis.read(body);
            return body;
        }
        finally {
            fis.close();
        }
    }

    private static byte[] slurpFile(final File file) throws IOException {
        final byte[] body = readFile(file);
        file.delete();
        return body;
    }

    private static void deleteFile(final File file) throws IOException {
        file.delete();
    }
}
