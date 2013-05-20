package io.jvm.zip.reader;


import io.jvm.zip.bytearray.OffsetRecord;
import io.jvm.zip.reader.centraldirectory.CentralDirectoryReader;
import io.jvm.zip.reader.localfile.LocalFileRecordReader;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryEnd;
import io.jvm.zip.structure.centraldirectory.CentralDirectoryRecord;

public class ZipReader extends OffsetRecord {

  public final LocalFileRecordReader[] localFileRecords;
  public final CentralDirectoryReader centralDirectory;

  public ZipReader(byte[] body) {
    super(body);

    int OFF_CDE = OFF_CentralDirectoryEndRecord();
    if (OFF_CDE >= 0) {
      CentralDirectoryEnd centralDirectoryEndRecord = new CentralDirectoryEnd(body, OFF_CDE);
      centralDirectory = new CentralDirectoryReader(centralDirectoryEndRecord);
      localFileRecords = new LocalFileRecordReader[centralDirectory.endRecord.getCentralDirectoryNumberOfEntrys()];

      int index = 0;
      for (CentralDirectoryRecord cdr : centralDirectory.records) {
        localFileRecords[index] = LocalFileRecordReader.fromCentralDirectoryRecord(cdr);
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

    return searchBackwards(CentralDirectoryEnd.HeaderSignature, startPos, endPos);
  }

  public int getLength() {
    return body.length;
  }
}
