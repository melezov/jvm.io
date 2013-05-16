package io.jvm.zip.writer.centraldirectory;

import io.jvm.zip.reader.centraldirectory.CentralDirectoryReader;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryEnd;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryRecord;

public class CentralDirectoryWriter {

  public final CentralDirectoryRecord[] records;
  public final CentralDirectoryEnd endRecord;

  public CentralDirectoryWriter(final CentralDirectoryReader r, final int offset) {
    records = new CentralDirectoryRecord[r.records.length];
    endRecord = new CentralDirectoryEnd(r.endRecord.)
  }

  public int getLength() {
    return 0;
  }
}
