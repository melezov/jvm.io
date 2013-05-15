package jvm.io.zip.structure.directory;

import jvm.io.zip.structure.ByteArrayOffsetObject;
import jvm.io.zip.structure.ByteArrayRange;

public class CentralDirectoryEndRecord extends ByteArrayOffsetObject {

  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x05, 0x06 };

  public static final int OFF_DiskNumber                              =  4;
  public static final int OFF_StartOfCentralDirectory_DiskNumber      =  6;
  public static final int OFF_NumberOfCentralDirectoryEntrys_ThisDisk =  8;
  public static final int OFF_NumberOfCentralDirectoryEntrys          = 10;
  public static final int OFF_CentralDirectory_Length                 = 12;
  public static final int OFF_CentralDirectory_Start                  = 16;
  public static final int OFF_Comment_Length                          = 20;
  public static final int OFF_Comment                                 = 22;

  public int getCommentLength() {
    return getShort(OFF_Comment_Length);
  }

  public ByteArrayRange getComment() {
    return new ByteArrayRange(body, OFF_Comment, getCommentLength());
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

  public CentralDirectoryEndRecord(byte[] body, int offset) {
    super(body, offset);
  }

}
