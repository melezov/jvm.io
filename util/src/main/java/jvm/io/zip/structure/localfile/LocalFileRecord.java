package jvm.io.zip.structure.localfile;

import jvm.io.zip.structure.ByteArrayOffsetObject;
import jvm.io.zip.structure.directory.CentralDirectoryFileRecord;

public class LocalFileRecord extends ByteArrayOffsetObject {

  public final LocalFileHeader localFile;
  public final LocalFileHeaderExtended localFileExtended;


  public LocalFileRecord(final byte[] body, final int offset) {
    super(body, offset);
    localFile = new LocalFileHeader(body, offset);

    LocalFileHeaderExtended tmpLFHE = new LocalFileHeaderExtended(body, offset + localFile.getLength());
    if (tmpLFHE.startsWith(LocalFileHeaderExtended.HeaderSignature)) {
      localFileExtended = tmpLFHE;
    } else {
      localFileExtended = null;
    }
  }

  public boolean hasExtendedHeader() {
    return localFileExtended != null;
  }

  public int getLength() {
    return localFile.getLength() + (hasExtendedHeader() ? localFileExtended.getLength() : 0);
  }

  public static LocalFileRecord fromCentralDirectoryRecord(CentralDirectoryFileRecord cdr) {
    return new LocalFileRecord(cdr.body, cdr.getLocalFileHeaderOffset());
  }

  @Override
  public String toString() {
    return localFile.toString();
  }
}
