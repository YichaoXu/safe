package edu.jhu.mssi.seclab.extsafe.autonode.csv.query

import com.github.tototoshi.csv.CSVReader
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.AbsEntity

import java.io.File

abstract class AbsQuerier

abstract class AbsCsvQuerier[E <: AbsEntity](private val jFiles: File) extends AbsQuerier{

  protected def with_csv[Output](behavior: CSVReader => Output): Output = {
    val reader = CSVReader.open(jFiles)
    val output = behavior(reader)
    reader.close()
    output
  }

  val all: Stream[E]
  def any(satisfied: E => Boolean): Stream[E] = all.filter(satisfied)
  def first(satisfied: E => Boolean): Option[E] = all.find(satisfied)

}