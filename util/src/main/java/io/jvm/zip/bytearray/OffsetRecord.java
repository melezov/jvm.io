package io.jvm.zip.bytearray;

import java.util.Arrays;

import io.jvm.zip.bytearray.tools.ByteArrayTool;

public abstract class OffsetRecord implements IOffsetRecord {

  public final byte[] body;
  public final int offset;

  public OffsetRecord(final byte[] body, final int offset) {
    this.body = body;
    this.offset = offset;
  }

  public OffsetRecord(final byte[] body) {
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

  protected String getString(final int index, final int length) throws IllegalArgumentException {
    if (index >= body.length - length) throw new IllegalArgumentException();
//    System.out.println("BODY SIZE = " + body.length);
//    System.out.println("OFFSET    = " + length);
//    System.out.println("LENGTH    = " + getLength());
//
//    System.out.println("INDEX     = " + index);
//    System.out.println("LENGTH    = " + length);
//
//    System.out.println("OFF INDEX = " + (offset + index));
//    System.out.println("OFF LEN   = " + (offset + index + length));

    return new String(body, offset + index, length);
  }

  protected void setShort(final int index, final int value) throws IllegalArgumentException {
    if (index >= body.length - 2) throw new IllegalArgumentException();
    ByteArrayTool.writeShortLittleEndian(body, offset + index, (short)value);
  }

  protected void setInt(final int index, final int value) throws IllegalArgumentException {
    if (index >= body.length - 4) throw new IllegalArgumentException();
    ByteArrayTool.writeIntLittleEndian(body, offset + index, value);
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

  protected byte[] getByteRange(final int index, final int length) {
    return Arrays.copyOfRange(body, offset + index, offset + index + length);
  }

  public abstract int getLength();

  public byte[] getBytes() {
    return getByteRange(0, getLength());
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

        if (found) return pos; // return position at witch first occurence of pattern was found
      }

    }

    return -1; // not found
  }
}
