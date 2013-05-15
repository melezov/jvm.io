package jvm.io.zip.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import jvm.io.zip.ZipReader;
import jvm.io.zip.structure.directory.CentralDirectoryFileRecord;
import jvm.io.zip.structure.localfile.LocalFileRecord;

public class Test {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    RandomAccessFile f;
    try {
      f = new RandomAccessFile("/home/dinko/workspace-code/test.jar", "r");
      byte[] body = new byte[(int)f.length()];
      f.read(body);
      f.close();

      ZipReader zip = new ZipReader(body);

      System.out.println("CENTRAL DIRECTORY RECORDS [" + zip.centralDirectory.records.length + "] : \n");
      for (CentralDirectoryFileRecord r : zip.centralDirectory.records) {
        System.out.println(r.toString());
      }

      System.out.println("\nLOCAL FILE RECORDS [" + zip.localFileRecords.length + "] : \n");
      for (LocalFileRecord r : zip.localFileRecords) {
        System.out.println(r.toString());
      }

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
