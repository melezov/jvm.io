package io.jvm.jsad.wrapper;

import java.io.IOException;

public interface IByteArrayProcessor {

  public byte[] process(final byte[] array) throws IOException;
}
