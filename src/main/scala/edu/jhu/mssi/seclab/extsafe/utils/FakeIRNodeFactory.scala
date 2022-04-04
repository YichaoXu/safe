package edu.jhu.mssi.seclab.extsafe.utils

import kr.ac.kaist.safe.nodes.ast.{ ASTNode, ASTNodeInfo, Comment }
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.{ SourceLoc, Span }

class FakeIRNodeFactory {

  private var _fileName: String = _
  def fileName(newVal: String): FakeIRNodeFactory = {
    _fileName = newVal
    this
  }

  private var _beginLoc: SourceLoc = _
  def begin(newLine: Int = 0, newCol: Int = 0, newOff: Int = 0): FakeIRNodeFactory = {
    _beginLoc = SourceLoc(newLine, newCol, newOff)
    this
  }
  private var _endLoc: SourceLoc = _
  def end(newLine: Int = 0, newCol: Int = 0, newOff: Int = 0): FakeIRNodeFactory = {
    _endLoc = SourceLoc(newLine, newCol, newOff)
    this
  }

  private var _comment: Option[Comment] = None
  def comment(newVal: Comment): FakeIRNodeFactory = {
    _comment = Some(newVal)
    this
  }

  private var _span: Option[Span] = None
  def span(newVal: Span): FakeIRNodeFactory = {
    _span = Some(newVal)
    this
  }

  def build(): IRNode = {
    val fSpan = _span getOrElse Span(_fileName, _beginLoc, _endLoc)
    val astNode = new ASTNode {
      override val info: ASTNodeInfo = ASTNodeInfo(fSpan, _comment)
      override def toString(indent: Int): String = info.toString
    }
    new IRNode(astNode) { override def toString(indent: Int): String = astNode.toString() }
  }
}

