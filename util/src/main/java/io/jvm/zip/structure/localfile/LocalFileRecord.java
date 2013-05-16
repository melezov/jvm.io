package io.jvm.zip.structure.localfile;

import java.io.UnsupportedEncodingException;

import io.jvm.zip.bytearray.OffsetObject;

public class LocalFileRecord extends OffsetObject {
  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x03, 0x04 };

  // OFFSETS
  private static final int OFF_CRC32                    = 14;
  private static final int OFF_CompressedData_Length    = 18;
  private static final int OFF_UnCompressedData_Length  = 18;
  private static final int OFF_FileName_Length          = 26;
  private static final int OFF_ExtraField_Length        = 28;
  private static final int OFF_FileName                 = 30;

  private final int OFF_ExtraField()      { return OFF_FileName + getFileNameLength(); }
  private final int OFF_CompressedData()  { return  OFF_ExtraField() + getExtraFieldLength(); }
  // END OFFSETS

  // LENGHTS

  public LocalFileRecord(final byte[] body, final int offset) {
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

  // WRITE
  public void setCRC32(final int CRC32) {
    setShort(OFF_CRC32, CRC32);
  }

  public void setCompressedDataLength(final int length) {
    setShort(OFF_CompressedData_Length, length);
  }

  public void setUnCompressedDataLength(final int length) {
    setShort(OFF_UnCompressedData_Length, length);
  }
  // END WRITE

  // RANGES
  public byte[] getOnlyHeaderData() {
    return getByteRange(offset, getOnlyHeaderLength());
  }

  public byte[] getCompressedData() {
    return getByteRange(OFF_CompressedData(), getCompressedDataLength());
  }

  private String getFileName() {
    try {
      return getString(OFF_FileName, getFileNameLength());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public byte[] getBytes() {
    return getByteRange(offset, getLength());
  }
  // END RANGES

  @Override
  public String toString() {
    return getFileName();
  }
}