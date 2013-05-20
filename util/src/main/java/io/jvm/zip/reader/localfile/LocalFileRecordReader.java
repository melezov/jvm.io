package io.jvm.zip.reader.localfile;

import io.jvm.zip.bytearray.OffsetRecord;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryRecord;
import io.jvm.zip.structure.localfile.LocalFileRecordExt;
import io.jvm.zip.structure.localfile.LocalFileRecord;

public class LocalFileRecordReader extends OffsetRecord {

  public final LocalFileRecord    localFileRecord;
  public final LocalFileRecordExt localFileRecordExt;

  public LocalFileRecordReader(final byte[] body, final int offset) {
    super(body, offset);
    localFileRecord = new LocalFileRecord(body, offset);

    LocalFileRecordExt ext = new LocalFileRecordExt(body, offset + localFileRecord.getLength());
    if (ext.startsWith(LocalFileRecordExt.HeaderSignature)) {
      localFileRecordExt = ext;
    } else {
      localFileRecordExt = null;
    }
  }

  public boolean hasExtendedHeader() {
    return localFileRecordExt != null;
  }

  public static LocalFileRecordReader fromCentralDirectoryRecord(CentralDirectoryRecord cdr) {
    return new LocalFileRecordReader(cdr.body, cdr.getLocalFileHeaderOffset());
  }

  @Override
  public String toString() {
    return localFileRecord.toString();
  }

  public int getLength() {
    // TODO Auto-generated method stub
    return 0;
  }
}
