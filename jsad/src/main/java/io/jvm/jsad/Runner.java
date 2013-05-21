package io.jvm.jsad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

class Runner {
    public final File workingDir;
    public final int waitPeriod;

    public Runner(
            final File workingDir,
            final int waitPeriod) {
        this.workingDir = workingDir;
        this.waitPeriod = waitPeriod;
    }

    public Runner() {
        this(new File("."), 0);
    }

    public RunnerOutput exec(
            final String[] params) {
        return exec(params, null);
    }

    public RunnerOutput exec(
            final String[] params,
            final byte[] input) {

        final ProcessBuilder pb = new ProcessBuilder(params);

        pb.directory(workingDir);



//                    val pio = new ProcessIO(
//                      in = transferFully(iS, _)
//                    , out = transferFully(_, oS)
//                    , err = transferFully(_, eS)
//                    )
        try {
          final Process process = pb.start();

          final ByteArrayOutputStream iS = (ByteArrayOutputStream) process.getOutputStream();
          final ByteArrayInputStream oS = (ByteArrayInputStream) process.getInputStream();
          final ByteArrayInputStream eS = (ByteArrayInputStream) process.getErrorStream();

          final int retCode = process.exitValue();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        return null;
    }
}

//import sys.process._
//import sys.process.BasicIO.transferFully
//
//import java.io.{ File, ByteArrayInputStream, ByteArrayOutputStream }
//import java.lang.{ Thread, Runnable, InterruptedException }
//
//object Runner extends Runner(new File("."), None) {
//  def after(time: Long)(cont: => Any): () => Boolean = {
//    val t = spawnThread {
//      try { Thread sleep time; cont }
//      catch { case _: InterruptedException => }
//    }
//    () => t.isAlive && { t.interrupt(); true }
//  }
//
//  def spawnThread(body: => Any) = {
//    val t = new Thread(new Runnable {
//      def run { body }
//    })
//    t.start()
//    t
//  }
//}
//
//case class Runner private(
//    workingDir: File
//  , waitPeriod: Option[Int]) {
//
//  def setWorkingDir(workingDir: File) =
//    copy(workingDir = workingDir)
//
//  def setTimeout(timeout: Int) =
//    copy(waitPeriod = Some(timeout))
//
//  def clearTimeout() =
//    copy(waitPeriod = None)
//
//  def apply(cmd: Seq[String], input: Array[Byte] = Array()) = {
//    val pb = Process(cmd, workingDir)
//
//    val iS = new ByteArrayInputStream(input)
//    val oS = new ByteArrayOutputStream()
//    val eS = new ByteArrayOutputStream()
//
//    val pio = new ProcessIO(
//      in = transferFully(iS, _)
//    , out = transferFully(_, oS)
//    , err = transferFully(_, eS)
//    )
//
//    val process = pb run pio
//
//    val retCode =
//      waitPeriod match {
//        case Some(timeout) =>
//          val unmonitor = Runner.after(timeout)(process.destroy)
//          val rC = process.exitValue
//          unmonitor()
//          rC
//
//        case _ =>
//          process.exitValue()
//      }
//
//    FilePipeOutput(
//      retCode
//    , oS.toByteArray
//    , eS.toByteArray
//    )
//  }
//}
