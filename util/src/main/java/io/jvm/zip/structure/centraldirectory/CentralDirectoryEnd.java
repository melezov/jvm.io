package io.jvm.zip.structure.centraldirectory;

import io.jvm.zip.bytearray.OffsetObject;
import io.jvm.zip.bytearray.Range;

public class CentralDirectoryEnd extends OffsetObject {

  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x05, 0x06 };

  private static final int OFF_DiskNumber                              =  4;
  private static final int OFF_StartOfCentralDirectory_DiskNumber      =  6;
  private static final int OFF_NumberOfCentralDirectoryEntrys_ThisDisk =  8;
  private static final int OFF_NumberOfCentralDirectoryEntrys          = 10;
  private static final int OFF_CentralDirectory_Length                 = 12;
  private static final int OFF_CentralDirectory_Start                  = 16;
  private static final int OFF_Comment_Length                          = 20;
  private static final int OFF_Comment                                 = 22;

  public int getCommentLength() {
    return getShort(OFF_Comment_Length);
  }

  public Range getComment() {
    return new Range(body, OFF_Comment, getCommentLength());
  }

  public int getCentralDirectoryStartOffset() {
    return getInt(OFF_CentralDirectory_Start);
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

}
