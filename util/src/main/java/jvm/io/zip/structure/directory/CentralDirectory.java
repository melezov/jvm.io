package jvm.io.zip.structure.directory;

import jvm.io.zip.structure.ByteArrayOffsetObject;

public class CentralDirectory extends ByteArrayOffsetObject {

  public final CentralDirectoryFileRecord[] records;
  public final CentralDirectoryEndRecord endRecord;

  public CentralDirectory(final CentralDirectoryEndRecord cde) {
    super(cde.body, cde.getCentralDirectoryStartOffset());
    this.endRecord = cde;

    // lets get records
    int recordsCount = endRecord.getCentralDirectoryNumberOfEntrys();
    this.records = new CentralDirectoryFileRecord[recordsCount];

    records[0] = new CentralDirectoryFileRecord(body, endRecord.getCentralDirectoryStartOffset());
    int offsetSum = 0;
    for (int i = 1; i < recordsCount; i++) {
      offsetSum += records[i-1].getLength();
      records[i] = new CentralDirectoryFileRecord(body, offset + offsetSum);
    }
  }

  public int getLength() {
    return endRecord.getCentralDirectoryLength();
  }
}
