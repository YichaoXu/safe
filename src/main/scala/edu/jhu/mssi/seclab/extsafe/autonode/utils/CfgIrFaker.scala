package edu.jhu.mssi.seclab.extsafe.autonode.utils

import edu.jhu.mssi.seclab.extsafe.autonode.builder.AbsBuilder
import kr.ac.kaist.safe.nodes.ast.{ASTNode, ASTNodeInfo, Comment}
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.{SourceLoc, Span}

class CfgIrFaker() extends AbsBuilder {

  private var _fileName: String = _
  def fileName(newVal: String): CfgIrFaker = _selfReturn{
    _fileName = newVal
  }

  private var _beginLoc: SourceLoc = _
  def begin(newLine: Int = 0, newCol: Int = 0, newOff: Int = 0): CfgIrFaker = _selfReturn{
    _beginLoc = SourceLoc(newLine, newCol, newOff)
  }
  private var _endLoc: SourceLoc = _
  def end(newLine: Int = 0, newCol: Int = 0, newOff: Int = 0): CfgIrFaker = _selfReturn{
    _endLoc = SourceLoc(newLine, newCol, newOff)
  }

  private var _comment: Option[Comment] = None
  def comment(newVal: Comment): CfgIrFaker = _selfReturn{
    _comment = Some(newVal)
  }

  private var _span: Option[Span] = None
  def span(newVal: Span): CfgIrFaker = _selfReturn{
    _span = Some(newVal)
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

