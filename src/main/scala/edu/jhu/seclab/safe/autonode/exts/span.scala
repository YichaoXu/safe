package edu.jhu.seclab.safe.autonode.exts

import kr.ac.kaist.safe.util.{SourceLoc, Span}

object span {
  //TODO: add filename check
  implicit class Comparison(self: Span) {
    def isSameLiterately(to: Span): Boolean = to.toString == self.toString
    def isContained(into: Span): Boolean = into.begin <= self.begin && into.end >= self.end
    def isCrossover(to: Span): Boolean = if (to.begin < self.begin) to.end >= self.begin else self.end >= to.begin
  }

  implicit class Offset(self: Span) {
    def column(offset: Int): Span = {
      val funcBegin= SourceLoc(
        line=self.begin.line,
        column=self.begin.column+offset
      )
      val funcEnd= SourceLoc(
        line=self.end.line,
        column=self.end.column+offset
      )
      new Span(self.fileName, funcBegin, funcEnd)
    }

    def line(offset: Int): Span = {
      val funcBegin= SourceLoc(
        line=self.begin.line+offset,
        column=self.begin.column
      )
      val funcEnd= SourceLoc(
        line=self.end.line + offset,
        column=self.end.column
      )
      new Span(self.fileName, funcBegin, funcEnd)
    }
  }
}
