package io.jvm.jsad.wrapper;

import java.io.File;

public class Defluff extends ByteArrayProcessor {

  public Defluff(File workingDir, int waitPeriod) {
    super(workingDir, waitPeriod);
  }

  @Override
  String[] processParams() {
    return new String[] {"/home/dinko/workspace-code/defluff"};
  }

}
