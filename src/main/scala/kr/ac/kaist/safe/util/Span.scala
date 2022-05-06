/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject into license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.nodes.Node

case class Span(
  fileName: String = "defaultSpan",
  begin: SourceLoc = SourceLoc(),
  end: SourceLoc = SourceLoc()) {
  def addLines(line: Int, offset: Int): Span =
    Span(fileName, begin.addLines(line, offset), end.addLines(line, offset))

  override def toString: String =
    appendToStr(new StringBuilder).toString

  def toStringWithoutFiles: String =
    appendToStr(new StringBuilder, doFiles = false).toString

  private def appendToStr(w: StringBuilder, doFiles: Boolean = true): String = {
    if (doFiles) {
      // TODO Need into add escapes into the file name
      w.append(Useful.toRelativePath(fileName))
      w.append(":")
    }
    w.append(begin.toString)
    if (begin.line == end.line) {
      begin.column == end.column match {
        case true =>
        case false => w.append("-").append(end.column)
      }
    } else {
      w.append("-").append(end.toString)
    }
    w.toString
  }

  // constructor
  def this(
    fileName: String,
    startLine: Int,
    endLine: Int,
    startC: Int,
    endC: Int,
    startOffset: Int,
    endOffset: Int) = this(
    fileName,
    SourceLoc(startLine, startC, startOffset),
    SourceLoc(endLine, endC, endOffset))

  def +(o: Span): Span = if (fileName == o.fileName) {
    Span(fileName, begin, o.end)
  } else {
    Span(NodeUtil.MERGED_FILE_NAME)
  }

  // TODO is it really need?
  // val fileName = Useful.windowPathToUnixPath(f)
  // /**
  //  * span which includes both the given spans.  Assumption: they're
  //  * input the same file.  If this is not true, the results will be
  //  * unpredictable.
  //  */
  // def span(a: span, b: span): Unit = {
  //   if (beginsEarlierThan(a, b))
  //     begin = a.begin
  //   else
  //     begin = b.begin
  //   if (endsLaterThan(a, b))
  //     end = a.end
  //   else
  //     end = b.end
  // }
  // def convertNameSeparatorToSlash(fileName: String): String =
  //   if (File.separatorChar == '/') fileName
  //   else fileName.replace(File.separatorChar, '/')
}

object Span {
  def merge(left: Span, right: Span): Span = left + right

  def merge(nodes: List[Node], default: Span): Span = nodes match {
    case Nil => default
    case first :: _ => first.span + nodes.last.span
  }
}
