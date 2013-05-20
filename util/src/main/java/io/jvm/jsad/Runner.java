package io.jvm.jsad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class Runner {

  static class StreamGobbler extends Thread {
    InputStream is;
    String type;
    OutputStream os;

    StreamGobbler(InputStream is, String type) {
      this(is, type, null);
    }

    StreamGobbler(InputStream is, String type, OutputStream redirect) {
      this.is = is;
      this.type = type;
      this.os = redirect;
    }

    public void run() {
      try {
        byte[] buffer = new byte[1024];
        int length;

        while ((length = is.read(buffer)) != -1) {
          if (os != null) os.write(buffer, 0, length); //copy streams
          System.out.println(type + ">" + new String(buffer, 0, length));
        }

        if (os != null) os.flush();

      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

  public static byte[] Run(final String[] args, final int maxSecondsTillCompletion, final byte[] inputStream) {
    final ProcessBuilder pb = new ProcessBuilder(args);

    final Process process;
    try {
      process = pb.start();

      OutputStream outputstream = process.getOutputStream();
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      Timer timer = new Timer(); // new timer
      timer.schedule(new TimerTask() {
          @Override
          public void run() {
              process.destroy();
          }
      }, maxSecondsTillCompletion * 1000); // seconds

      // any error message?
      StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

      // any output?
      StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", output);

      // kick them off
      errorGobbler.start();
      outputGobbler.start();

      outputstream.write(inputStream);
      outputstream.close();

      int i = process.waitFor();
      System.out.println("Process exited with code : " + i);
      timer.cancel();

      output.flush();
      output.close();
      return output.toByteArray();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return null;
  }

}
