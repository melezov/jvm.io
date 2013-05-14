package jvm.io.zip.structure.localfile;

import jvm.io.zip.util.ByteBuffer;


public class LocalFileRecord extends ByteBuffer {

  public LocalFileHeader lfh;
  public LocalFileHeaderExtended elfh;


  public LocalFileRecord(final byte[] body, final int offset) {
    super(body, offset, length);
  }

}
