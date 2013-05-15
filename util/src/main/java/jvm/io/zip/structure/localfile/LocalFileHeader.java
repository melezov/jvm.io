package jvm.io.zip.structure.localfile;

import jvm.io.zip.structure.ByteArrayOffsetObject;
import jvm.io.zip.structure.ByteArrayRange;

public class LocalFileHeader extends ByteArrayOffsetObject {
  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x03, 0x04 };

  // OFFSETS
  private static final int OFF_CompressedData_Length  = 18;
  private static final int OFF_FileName_Length        = 26;
  private static final int OFF_ExtraField_Length      = 28;
  private static final int OFF_FileName               = 30;

  private final int OFF_ExtraField()      { return OFF_FileName + getFileNameLength(); }
  private final int OFF_CompressedData()  { return  OFF_ExtraField() + getExtraFieldLength(); }
  // END OFFSETS

  // LENGHTS
  public LocalFileHeader(final byte[] body, final int offset) {
    super(body, offset);
  }

  private int getFileNameLength() {
    return getShort(OFF_FileName_Length);
  }

  private int getExtraFieldLength() {
    return getShort(OFF_ExtraField_Length);
  }

  private int getCompressedDataLength() {
    return getInt(OFF_CompressedData_Length);
  }

  private int getOnlyHeaderLength() {
    return 30 + getFileNameLength() + getExtraFieldLength();
  }

  public int getLength() {
    return getOnlyHeaderLength() + getCompressedDataLength();
  }
  // END LENGHTS

  public ByteArrayRange getOnlyHeaderData() {
    return getByteRange(offset, getOnlyHeaderLength());
  }

  public ByteArrayRange getCompressedData() {
    return getByteRange(OFF_CompressedData(), getCompressedDataLength());
  }

  public ByteArrayRange getFileName() {
    return getByteRange(OFF_FileName, getFileNameLength());
  }

  @Override
  public String toString() {
    return getFileName().toString();
  }
}
