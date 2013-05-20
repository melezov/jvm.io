package io.jvm.zip.writer.localfile;

import io.jvm.zip.reader.localfile.LocalFileRecordReader;
import io.jvm.zip.structure.localfile.CompressedData;
import io.jvm.zip.structure.localfile.LocalFileRecord;

public class LocalFileRecordWriter {

  public final LocalFileRecord localRecord;

  public LocalFileRecordWriter(final LocalFileRecordReader r, final CompressedData newCompressedData) {
    localRecord = new LocalFileRecord(r.localFileRecord.getBytes(), 0);

    if (r.hasExtendedHeader()) localRecord.setUnCompressedDataLength(r.localFileRecordExt.getUnCompressedFileLength());
    localRecord.setCompressedDataObject(newCompressedData);
  }
}
