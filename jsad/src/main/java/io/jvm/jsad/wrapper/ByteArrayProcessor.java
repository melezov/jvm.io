package io.jvm.jsad.wrapper;

import io.jvm.jsad.Runner;
import io.jvm.jsad.RunnerOutput;

import java.io.File;
import java.io.IOException;

public abstract class ByteArrayProcessor implements IByteArrayProcessor {

  private final Runner runner;

  public ByteArrayProcessor(
      final File workingDir,
      final int waitPeriod) {

    runner = new Runner(workingDir, waitPeriod);
  }

  abstract String[] processParams();

  public byte[] process(byte[] array) throws IOException {
    RunnerOutput out = runner.exec(processParams(), array);
    if (out.code != 0) throw new IOException("Process ended with code " + out.code + "\n" + new String(out.error));
    if (out.error.length != 0) throw new IOException(new String(out.error));
    return out.output;
  }
}
