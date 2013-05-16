package io.jvm.zip.writer;

import io.jvm.zip.reader.ZipReader;
import io.jvm.zip.reader.localfile.LocalFileRecordReader;
import io.jvm.zip.structure.localfile.LocalFileRecord;
import io.jvm.zip.structure.localfile.LocalFileRecordExt;
import io.jvm.zip.writer.centraldirectory.CentralDirectoryWriter;
import io.jvm.zip.writer.localfile.LocalFileRecordWriter;

public class ZipWriter {

  public final LocalFileRecordWriter[] records;
  private final CentralDirectoryWriter directory;

  public ZipWriter(ZipReader reader, byte[][] newRawData) {
    // set records for writing
    records = new LocalFileRecordWriter[reader.localFileRecords.length];

    int offsetSum = 0;
    for(int index = 0; index < reader.localFileRecords.length; index++) {
      records[index] = new LocalFileRecordWriter(reader.localFileRecords[index], newRawData[index], offsetSum);
      offsetSum += records[index].data.getLength();
    }

    directory = new CentralDirectoryWriter(reader.centralDirectory);

    // set central directory
    for(int index = 0; index < reader.localFileRecords.length; index++) {
      directory.records[index] = new CentralDirectoryRecordWriter(reader.centralDirectory.records[index]);

    }
  }


  public boolean writeToFile(String fileName) {
    return false;
  }
}
