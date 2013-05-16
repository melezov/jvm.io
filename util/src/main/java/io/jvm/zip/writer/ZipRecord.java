package io.jvm.zip.writer;

import io.jvm.zip.structure.centraldirectory.CentralDirectoryRecord;
import io.jvm.zip.structure.localfile.LocalFileRecord;

public class ZipRecord {

  private LocalFileRecord localFileRecord;
  private CentralDirectoryRecord centralDirectoryRecord;

  public ZipRecord(final ZipRecord previous) {

  }


}
