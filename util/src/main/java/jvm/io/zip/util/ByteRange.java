package jvm.io.zip.util;

public class ByteRange extends ByteBuffer {
  public final int length;

  public ByteRange(final byte[] bytes, final int offset, final int length) {
    super(bytes, offset);
    this.length = length;
  }
}
