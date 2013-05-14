package jvm.io.zip;

import jvm.io.zip.structure.directory.*;
import jvm.io.zip.structure.localfile.*;
import jvm.io.zip.util.ByteBuffer;

public class ZipFile extends ByteBuffer {

  public LocalFileRecord[] localFileRecords;
  public CentralDirectory centralDirectory;
  public CentralDirectoryEndRecord centralDirectoryEndRecord;

  public ZipFile(byte[] body) {
    super(body);
  }

  public boolean CreateStructure() {
    int OFF_CDE = OFF_CentralDirectoryEndRecord();
    if (OFF_CDE >= 0) {
      centralDirectoryEndRecord = new CentralDirectoryEndRecord(this, OFF_CDE);
      return true;
    } else {
      return false; // init failed
    }
  }


  private int OFF_CentralDirectoryEndRecord() {
    int startPos = body.length - 22; // start position of search for EndOfCentralDirectoryRecord signature
    int endPos = startPos - 65536; // end position of search
    if (endPos < 0) endPos = 0; // end position cant be smaller than 0

    return searchBackwards(CentralDirectoryEndRecord.HeaderSignature, startPos, endPos);
  }
}
