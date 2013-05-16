package io.jvm.zip.writer.localfile;

import io.jvm.zip.bytearray.tools.ByteArrayTool;
import io.jvm.zip.reader.localfile.LocalFileRecordReader;
import io.jvm.zip.structure.localfile.LocalFileRecord;

public class LocalFileRecordWriter {

  public final int offset;
  public final LocalFileRecord data;

  public LocalFileRecordWriter(final LocalFileRecordReader r, final byte[] newCompressedData, final int newOffset) {
    offset = newOffset;

    byte[] header = r.localFile.getOnlyHeaderData();

    data = new LocalFileRecord(ByteArrayTool.ConcatArrays(header, newCompressedData), 0);

    if (r.hasExtendedHeader()) data.setUnCompressedDataLength(r.localFileExt.getUnCompressedFileLength());

    data.setCRC32(ByteArrayTool.computeCRC32(newCompressedData));
    data.setCompressedDataLength(newCompressedData.length);
  }

}
