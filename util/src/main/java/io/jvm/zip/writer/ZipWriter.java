package io.jvm.zip.writer;

import io.jvm.zip.reader.ZipReader;
import io.jvm.zip.structure.localfile.LocalFileRecord;
import io.jvm.zip.writer.centraldirectory.CentralDirectoryWriter;
import io.jvm.zip.writer.localfile.LocalFileRecordWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ZipWriter {

  public final LocalFileRecordWriter[] records;
  private final CentralDirectoryWriter directory;

  public ZipWriter(ZipReader reader) {
    // set records for writing
    records = new LocalFileRecordWriter[reader.localFileRecords.length];

//    for(int index = 0; index < reader.localFileRecords.length; index++) {
//      records[index] = new LocalFileRecordWriter(reader.localFileRecords[index]);
//    }

    directory = new CentralDirectoryWriter(reader.centralDirectory);
  }

//  public void changeCompressedData(CompressedData[] data) {
//    for (int index = 0; index < data.length; index++) {
//      records[index].newCompressedData = data[index];
//    }
//    UpdateCentralDirectory();
//  }

  public void UpdateCentralDirectory() {
    directory.UpdateCentralDirectory(records);
  }

  public void writeToFile(String fileName) {

    BufferedOutputStream bs = null;

    try {

        FileOutputStream fs = new FileOutputStream(new File(fileName));
        bs = new BufferedOutputStream(fs);

        // write local file records
        for (int index = 0; index < records.length; index++) {
          LocalFileRecord r = records[index].localRecord;

          bs.write(r.getBytes());
        }

        // write central directory
        bs.write(directory.getBytes());

        bs.close();
        bs = null;

    } catch (Exception e) {
        e.printStackTrace();
    }

    if (bs != null) try { bs.close(); } catch (Exception e) {}
  }
}
