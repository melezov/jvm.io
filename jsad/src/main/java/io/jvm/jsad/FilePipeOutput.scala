package org.revenj.jsad

case class FilePipeOutput(
    code: Int
  , output: Array[Byte]
  , error: Array[Byte])
