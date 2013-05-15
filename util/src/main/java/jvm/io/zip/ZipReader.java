package jvm.io.zip;

import jvm.io.zip.structure.ByteArrayOffsetObject;
import jvm.io.zip.structure.directory.*;
import jvm.io.zip.structure.localfile.*;

public class ZipReader extends ByteArrayOffsetObject {

  public final LocalFileRecord[] localFileRecords;
  public final CentralDirectory centralDirectory;

  public ZipReader(byte[] body) {
    super(body);

    int OFF_CDE = OFF_CentralDirectoryEndRecord();
    if (OFF_CDE >= 0) {
      CentralDirectoryEndRecord centralDirectoryEndRecord = new CentralDirectoryEndRecord(body, OFF_CDE);
      centralDirectory = new CentralDirectory(centralDirectoryEndRecord);
      localFileRecords = new LocalFileRecord[centralDirectory.endRecord.getCentralDirectoryNumberOfEntrys()];

      int index = 0;
      for (CentralDirectoryFileRecord cdr : centralDirectory.records) {
        localFileRecords[index] = LocalFileRecord.fromCentralDirectoryRecord(cdr);
        index++;
      }

    } else {
      localFileRecords = null;
      centralDirectory = null;
    }

  }

  public boolean hasCentralDirectory() {
    return centralDirectory != null;
  }

  private int OFF_CentralDirectoryEndRecord() {
    int startPos = body.length - 22; // start position of search for EndOfCentralDirectoryRecord signature
    int endPos = startPos - 65536; // end position of search
    if (endPos < 0) endPos = 0; // end position cant be smaller than 0

    return searchBackwards(CentralDirectoryEndRecord.HeaderSignature, startPos, endPos);
  }
}
