package io.jvm.zip.structure.centraldirectory;

import io.jvm.zip.bytearray.OffsetRecord;

public class CentralDirectoryEnd extends OffsetRecord {

  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x05, 0x06 };

  private static final int OFF_NumberOfCentralDirectoryEntrys          = 10;
  private static final int OFF_CentralDirectory_Length                 = 12;
  private static final int OFF_CentralDirectory_Start                  = 16;
  private static final int OFF_Comment_Length                          = 20;
  private static final int OFF_Comment                                 = 22;

  private int getCommentLength() {
    return getShort(OFF_Comment_Length);
  }

  public String getComment() {
    return getString(OFF_Comment, getCommentLength());
  }

  public int getCentralDirectoryStartOffset() {
    return getInt(OFF_CentralDirectory_Start);
  }

  public void setCentralDirectoryStartOffset(final int offset) {
    setInt(OFF_CentralDirectory_Start, offset);
  }

  public int getCentralDirectoryNumberOfEntrys() {
    return getShort(OFF_NumberOfCentralDirectoryEntrys);
  }

  public int getCentralDirectoryLength() {
    return getInt(OFF_CentralDirectory_Length);
  }

  public CentralDirectoryEnd(byte[] body, int offset) {
    super(body, offset);
  }

  public int getLength() {
    return 22 + getCommentLength();
  }
}
