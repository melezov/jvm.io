package jvm.io.zip.structure;

import jvm.io.zip.util.ByteArrayTool;

public abstract class ByteArrayOffsetObject {
  public final byte[] body;
  public final int offset;

  public ByteArrayOffsetObject(final byte[] body, final int offset) {
    this.body = body;
    this.offset = offset;
  }

  public ByteArrayOffsetObject(final byte[] body) {
    this(body, 0);
  }

  protected int getShort(final int index) throws IllegalArgumentException {
    if (index >= body.length - 2) throw new IllegalArgumentException();
    return ByteArrayTool.readShortLittleEndian(body, offset + index);
  }

  protected int getInt(final int index) throws IllegalArgumentException {
    if (index >= body.length - 4) throw new IllegalArgumentException();
    return ByteArrayTool.readIntLittleEndian(body, offset + index);
  }

  public boolean startsWith(final byte[] pattern) throws IllegalArgumentException {
    if (pattern.length > body.length) throw new IllegalArgumentException();
    if (body[offset + 0] == pattern[0]) {
      for (int i=1; i < pattern.length; i++) {
        if (body[offset + i] != pattern[i]) return false;
      }
    }
    return true;
  }

  protected ByteArrayRange getByteRange(final int index, final int length) {
    return new ByteArrayRange(body, offset + index, length);
  }

  // compares bytes in bytebuffer starting at offset with bytes given as parameter
  protected int searchBackwards(final byte[] pattern, final int startPos, final int endPos) throws IllegalArgumentException {
    if (endPos + pattern.length > body.length) throw new IllegalArgumentException();
    if (startPos + pattern.length > body.length) throw new IllegalArgumentException();

    final byte p0 = pattern[0]; // take last char in pattern

    boolean found;

    for (int pos = offset + startPos; pos >= offset + endPos; pos--) { // traverse range backwards

      if (body[pos] == p0) {
        found = true;

        for (int i = 1; i < pattern.length; i++) {
          if (body[pos + i] != pattern[i]) {
            found = false;
            break;
          }
        }

        if (found) return pos;
      }

    }

    return -1; // not found
  }
}
