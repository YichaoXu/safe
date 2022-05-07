package edu.jhu.seclab.safe.autonode.exts

import kr.ac.kaist.safe.util.Span

object span {
  implicit class Comparison(self: Span) {
    def isContained(into: Span): Boolean = into.begin <= self.begin && into.end >= self.end
    def isCrossover(to: Span): Boolean = if (to.begin < self.begin) to.end >= self.begin else self.end >= to.begin
  }
}
