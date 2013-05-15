package jvm.io.zip.structure;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteArrayRange extends ByteArrayOffsetObject {
  public final int length;

  public ByteArrayRange(final byte[] bytes, final int offset, final int length) {
    super(bytes, offset);
    this.length = length;
  }

  public byte[] getBytes() {
    return Arrays.copyOfRange(body, offset, offset + length);
  }

  @Override
  public String toString() {
    try {
      // TODO ENCODING !!!
      return new String(getBytes(), System.getProperty("file.encoding"));
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }
}
