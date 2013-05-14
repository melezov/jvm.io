package jvm.io.zip.structure.directory;

import jvm.io.zip.util.ByteBuffer;
import jvm.io.zip.util.ByteRange;

public class CentralDirectoryFileRecord extends ByteBuffer {

  public static final byte[] HeaderSignature = { 0x02, 0x01, 0x4b, 0x50 };

  // OFFSETS
  private static final int OFF_FileName_Length        = 28;
  private static final int OFF_ExtraField_Length      = 30;
  private static final int OFF_FileComment_Length     = 32;
  private static final int OFF_FileName               = 46;

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

  public ByteBuffer getFileName() {
    return getByteBuffer(OFF_FileName, getFileNameLength());
  }

  public ByteBuffer getExtraField() {
    return getByteBuffer(OFF_ExtraField(), getExtraFieldLength());
  }

  public ByteBuffer getFileComment() {
    return new ByteRange(OFF_FileComment(), getFileCommentLength());
  }

  public int byteLength() {
    return 46 + getFileNameLength() + getExtraFieldLength() + getFileCommentLength();
  }
}
