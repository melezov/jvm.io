package io.jvm.zip.structure.localfile;

import io.jvm.zip.bytearray.IOffsetRecord;
import io.jvm.zip.bytearray.tools.ByteArrayTool;

public class CompressedData implements IOffsetRecord {

  public final byte[] Data;
  public final int Method;
  public final int CRC32;

  public CompressedData(final byte[] body, final int method, final int CRC32) {
    this.Data = body;
    this.Method = method;
    this.CRC32 = CRC32;
  }

  public CompressedData(final byte[] body, final int method) {
    this.Data = body;
    this.Method = method;
    this.CRC32 = ByteArrayTool.computeCRC32(body, 0, body.length);
  }

  @Override
  public int getLength() {
    return Data.length;
  }

  @Override
  public byte[] getBytes() {
    return Data;
  }
}
