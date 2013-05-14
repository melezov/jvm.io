package jvm.io.zip.structure.localfile;

import jvm.io.zip.util.ByteBuffer;

public class LocalFileHeader extends ByteBuffer {

  public static final byte[] HeaderSignature = { 0x04, 0x03, 0x4b, 0x50 };

  // OFFSETS
  private static final int OFF_CompressedData_Length  = 18;
  private static final int OFF_FileName_Length        = 26;
  private static final int OFF_ExtraField_Length      = 28;
  private static final int OFF_FileName               = 30;

  private final int OFF_ExtraField()      { return OFF_FileName + getFileNameLength(); }
  private final int OFF_CompressedData()  { return  OFF_ExtraField() + getExtraFieldLength(); }
  // END OFFSETS


  public LocalFileHeader(final byte[] body, final int offset) {
    super(body, offset);
    this.length = 30 + getFileNameLength() + getExtraFieldLength() + getCompressedDataLength();
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

  public ByteBuffer getCompressedData() {
    return getByteBuffer(OFF_CompressedData(), getCompressedDataLength());
  }

}
