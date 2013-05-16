package io.jvm.zip.reader.centraldirectory;

import io.jvm.zip.bytearray.OffsetObject;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryEnd;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryRecord;

public class CentralDirectoryReader extends OffsetObject {

  public final CentralDirectoryRecord[] records;
  public final CentralDirectoryEnd endRecord;

  public CentralDirectoryReader(final CentralDirectoryEnd cde) {
    super(cde.body, cde.getCentralDirectoryStartOffset());
    this.endRecord = cde;

    // lets get records
    int recordsCount = endRecord.getCentralDirectoryNumberOfEntrys();
    this.records = new CentralDirectoryRecord[recordsCount];

    records[0] = new CentralDirectoryRecord(body, endRecord.getCentralDirectoryStartOffset());
    int offsetSum = 0;
    for (int i = 1; i < recordsCount; i++) {
      offsetSum += records[i-1].getLength();
      records[i] = new CentralDirectoryRecord(body, offset + offsetSum);
    }
  }

  public int getLength() {
    return endRecord.getCentralDirectoryLength();
  }
}
