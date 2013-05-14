package jvm.io.zip.structure.localfile;

import jvm.io.zip.util.ByteBuffer;

public class LocalFileHeaderExtended extends ByteBuffer {

  public static final byte[] HeaderSignature = { 0x08, 0x07, 0x4b, 0x50 }; // Optional

  // OFFSETS
  private final int OFF_CRC32; // depending on type of ELFH can start at 0 or at 4 offset
  private final int OFF_Compressed_Length()    { return OFF_CRC32 + 4; }
  private final int OFF_UnCompressed_Length()  { return OFF_Compressed_Length() + 4; }
  // END OFFSETS


  public LocalFileHeaderExtended(final byte[] body, final int offset, final int length) {
    super(body, offset, length);
    if (this.startsWith(HeaderSignature)) {
      OFF_CRC32 = 4;
    } else {
      OFF_CRC32 = 0;
    }
  }

  public int getCRC32() {
    return getInt(OFF_CRC32);
  }

  public int getCompressedFileLength() {
    return getInt(OFF_Compressed_Length());
  }

  public int getUnCompressedFileLength() {
    return getInt(OFF_UnCompressed_Length());
  }
}
