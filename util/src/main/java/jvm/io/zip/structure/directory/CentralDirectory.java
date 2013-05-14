package jvm.io.zip.structure.directory;

import jvm.io.zip.util.ByteBuffer;

public class CentralDirectory extends ByteBuffer {

  public final CentralDirectoryFileRecord[] Records;
  public final CentralDirectoryEndRecord EndRecord;

  public CentralDirectory(final CentralDirectoryEndRecord cde) {
    super(cde.body, cde.getCentralDirectoryOffset());
    this.EndRecord = cde;

    // lets get records
    int recordsCount = EndRecord.getCentralDirectoryNumberOfEntrys();
    this.Records = new CentralDirectoryFileRecord[recordsCount];

    for (int i = 0; i < recordsCount; i++) {
      Records[i] = new CentralDirectoryFileRecord()
    }
  }
}
