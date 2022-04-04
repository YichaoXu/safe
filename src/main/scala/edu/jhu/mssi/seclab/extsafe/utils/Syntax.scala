package edu.jhu.mssi.seclab.extsafe.utils
import com.github.tototoshi.csv.CSVReader
import java.io.File

object Syntax {

  def with_csv[Output](jFiles: File)(behavior: CSVReader => Output): Output = {
    val reader = CSVReader.open(jFiles)
    val output = behavior(reader)
    reader.close()
    output
  }

  def with_multi_csv[Out](jFiles: File*)(behavior: Seq[CSVReader] => Out): Out = {
    val readers = jFiles.map(file => CSVReader.open(file))
    val output = behavior(readers)
    readers.foreach(_.close)
    output
  }

  def findLastFrom[A](la: Iterable[A])(f: A => Boolean): Option[A] =
    la.foldLeft(Option.empty[A]) { (acc, cur) =>
      if (f(cur)) Some(cur)
      else acc
    }

}

