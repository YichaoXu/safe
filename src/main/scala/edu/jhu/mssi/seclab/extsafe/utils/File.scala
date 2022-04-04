package edu.jhu.mssi.seclab.extsafe.utils

import java.io.File
import scala.io.Source

object File {
  def lines(jFile: File): Int = {
    val buffer = Source.fromFile(jFile)
    val count = buffer.getLines().size
    buffer.close()
    count
  }
}
