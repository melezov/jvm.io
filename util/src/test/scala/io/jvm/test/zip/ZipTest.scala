package io.jvm.test
package zip

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import jvm.io.zip.ZipReader
import java.io.FileInputStream


@RunWith(classOf[JUnitRunner])
class ZipTest extends FeatureSpec with GivenWhenThen with MustMatchers {
  feature("Zip Test"){

    scenario("Show File Records") {
      val fileName = "/home/dinko/workspace-code/test.jar"

      //val source = scala.io.Source.fromFile(fileName)
      //val body = source.toArray.map(_.toByte)
      //source.close()

      val is = new FileInputStream(fileName)
      val cnt = is.available
      val body = Array.ofDim[Byte](cnt)
      is.read(body)
      is.close()

      val zip = new ZipReader(body)
      zip.centralDirectory.records.foreach(println);
      assert (true)
    }
  }
}
