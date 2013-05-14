package jvm.io.zip.structure.directory;

import jvm.io.zip.util.ByteBuffer;
import jvm.io.zip.util.ByteRange;

public class CentralDirectoryEndRecord extends ByteBuffer {

  public static final byte[] HeaderSignature = { 0x06, 0x05, 0x4b, 0x50 };

  public static final int OFF_DiskNumber                              =  4;
  public static final int OFF_StartOfCentralDirectory_DiskNumber      =  6;
  public static final int OFF_NumberOfCentralDirectoryEntrys_ThisDisk =  8;
  public static final int OFF_NumberOfCentralDirectoryEntrys          = 10;
  public static final int OFF_CentralDirectory_Length                 = 12;
  public static final int OFF_CentralDirectory                        = 16;
  public static final int OFF_Comment_Length                          = 20;

  public int getCommentLength() {
    return getShort(OFF_Comment_Length);
  }

  public ByteRange getComment() {
    return new ByteRange(body, 22, getCommentLength());
  }

  public int getCentralDirectoryOffset() {
    return getInt(OFF_CentralDirectory);
  }

  public int getCentralDirectoryNumberOfEntrys() {
    return getShort(OFF_NumberOfCentralDirectoryEntrys);
  }

  public CentralDirectoryEndRecord(byte[] body, int offset) {
    super(body, offset);
  }

}
