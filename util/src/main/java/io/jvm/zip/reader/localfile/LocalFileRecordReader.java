package io.jvm.zip.reader.localfile;

import io.jvm.zip.bytearray.OffsetObject;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryRecord;
import io.jvm.zip.structure.localfile.LocalFileRecordExt;
import io.jvm.zip.structure.localfile.LocalFileRecord;

public class LocalFileRecordReader extends OffsetObject {

  public final LocalFileRecord    localFile;
  public final LocalFileRecordExt localFileExt;

  public LocalFileRecordReader(final byte[] body, final int offset) {
    super(body, offset);
    localFile = new LocalFileRecord(body, offset);

    LocalFileRecordExt ext = new LocalFileRecordExt(body, offset + localFile.getLength());
    if (ext.startsWith(LocalFileRecordExt.HeaderSignature)) {
      localFileExt = ext;
    } else {
      localFileExt = null;
    }
  }

  public boolean hasExtendedHeader() {
    return localFileExt != null;
  }

  public static LocalFileRecordReader fromCentralDirectoryRecord(CentralDirectoryRecord cdr) {
    return new LocalFileRecordReader(cdr.body, cdr.getLocalFileHeaderOffset());
  }

  @Override
  public String toString() {
    return localFile.toString();
  }
}
