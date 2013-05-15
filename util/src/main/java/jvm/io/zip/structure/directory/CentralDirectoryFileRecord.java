package jvm.io.zip.structure.directory;

import jvm.io.zip.structure.ByteArrayOffsetObject;
import jvm.io.zip.structure.ByteArrayRange;

public class CentralDirectoryFileRecord extends ByteArrayOffsetObject {
  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x01, 0x02 };

  // OFFSETS
  private static final int OFF_FileName_Length    = 28;
  private static final int OFF_ExtraField_Length  = 30;
  private static final int OFF_FileComment_Length = 32;
  private static final int OFF_LocalHeader        = 42;
  private static final int OFF_FileName           = 46;

  private final int OFF_ExtraField()  { return OFF_FileName + getFileNameLength(); }
  private final int OFF_FileComment() { return OFF_ExtraField() + getExtraFieldLength(); }
  // END OFFSETS


  public CentralDirectoryFileRecord(final byte[] body, final int offset) {
    super(body, offset);
  }

  private int getFileNameLength() {
    return getShort(OFF_FileName_Length);
  }

  private int getExtraFieldLength() {
    return getShort(OFF_ExtraField_Length);
  }

  private int getFileCommentLength() {
    return getInt(OFF_FileComment_Length);
  }

  public int getLocalFileHeaderOffset() {
    return getInt(OFF_LocalHeader);
  }

  public ByteArrayRange getFileName() {
    return getByteRange(OFF_FileName, getFileNameLength());
  }

  public ByteArrayRange getExtraField() {
    return getByteRange(OFF_ExtraField(), getExtraFieldLength());
  }

  public ByteArrayRange getFileComment() {
    return getByteRange(OFF_FileComment(), getFileCommentLength());
  }

  public int getLength() {
    return 46 + getFileNameLength() + getExtraFieldLength() + getFileCommentLength();
  }

  @Override
  public String toString() {
    return getFileName().toString();
  }
}
