package org.revenj.jsad

import sys.process._
import sys.process.BasicIO.transferFully

import java.io.{ File, ByteArrayInputStream, ByteArrayOutputStream }
import java.lang.{ Thread, Runnable, InterruptedException }

object Runner extends Runner(new File("."), None) {
  def after(time: Long)(cont: => Any): () => Boolean = {
    val t = spawnThread {
      try { Thread sleep time; cont }
      catch { case _: InterruptedException => }
    }
    () => t.isAlive && { t.interrupt(); true }
  }

  def spawnThread(body: => Any) = {
    val t = new Thread(new Runnable {
      def run { body }
    })
    t.start()
    t
  }
}

case class Runner private(
    workingDir: File
  , waitPeriod: Option[Int]) {

  def setWorkingDir(workingDir: File) =
    copy(workingDir = workingDir)

  def setTimeout(timeout: Int) =
    copy(waitPeriod = Some(timeout))

  def clearTimeout() =
    copy(waitPeriod = None)

  def apply(cmd: Seq[String], input: Array[Byte] = Array()) = {
    val pb = Process(cmd, workingDir)

    val iS = new ByteArrayInputStream(input)
    val oS = new ByteArrayOutputStream()
    val eS = new ByteArrayOutputStream()

    val pio = new ProcessIO(
      in = transferFully(iS, _)
    , out = transferFully(_, oS)
    , err = transferFully(_, eS)
    )

    val process = pb run pio

    val retCode =
      waitPeriod match {
        case Some(timeout) =>
          val unmonitor = Runner.after(timeout)(process.destroy)
          val rC = process.exitValue
          unmonitor()
          rC

        case _ =>
          process.exitValue()
      }

    FilePipeOutput(
      retCode
    , oS.toByteArray
    , eS.toByteArray
    )
  }
}
