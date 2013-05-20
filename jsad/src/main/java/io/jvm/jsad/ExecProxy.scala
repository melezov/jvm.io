package org.revenj.jsad

import scalax.io._
import scalax.file._

import org.revenj.ramdisk._
import hr.element.etb.io._

import scala.concurrent.ops.spawn
import sys.process._

object ExecProxy {
  private val isWindows =
    "\\" == sys.props("file.separator")

  private val proxyType =
    if (isWindows) "proxy.bat" else "proxy.sh"

  private def getProxy(name: String) =
    Resource.fromClasspath(name, getClass).slurpString

  private val proxy = getProxy(proxyType)

  def apply(cmd: Seq[String], input: Array[Byte]) = {
    val rD = RAMDisk.now.install()
    val rnd = XKCD.now.toString

    val iF = rD / (rnd +"-input.bin")
    val oF = rD / (rnd +"-output.bin")
    val eF = rD / (rnd +"-error.bin")
    val pF = rD / (rnd +'-'+ proxyType)

    iF.write(input)

    val proxyBody = proxy.format(iF.path, oF.path, eF.path)
    pF.write(proxyBody)

    val pL = ProcessLogger(out => (), err => ())
    val exitCode = pF.path +: cmd ! pL

    val out = oF.byteArray
    val err = eF.byteArray

    spawn {
      rD.uninstall()
    }

    FilePipeOutput(exitCode, out, err)
  }
}
