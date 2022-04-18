package edu.jhu.mssi.seclab.extsafe.autonode.csv.model

import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.EdgeType.EdgeType
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.NodeType.NodeType
import kr.ac.kaist.safe.util.{ SourceLoc, Span }

abstract sealed class AbsEntity

case class ModelEdge(private val data: Map[String, String]) extends AbsEntity {
  val start: Int = data("start:START_ID").toInt
  val end: Int = data("end:END_ID").toInt
  val edge_var: String = data("var")
  val taint_src: String = data("taint_src")
  val taint_dst: String = data("taint_dst")
  private val edgeTypeStr: String = data("type:TYPE")
  val edge_type: EdgeType = EdgeType(edgeTypeStr).getOrElse(EdgeType.NULL)

  def is(eType: EdgeType): Boolean = eType == edge_type
  def in(eTypes: EdgeType*): Boolean = eTypes.exists(eTypes => is(eTypes))
}

case class ModelNode(
  private val fileName: String,
  private val data: Map[String, String]) extends AbsEntity {

  val id: Int = data("ID").toInt
  val label: String = data("labels:label")
  val flags: Array[String] = data("flags:string[]").split(" ")
  val lineNumber: Int = data("lineno:int").toInt
  val code: String = data("code")
  val childNumber: Int = data("childnum:int").toInt
  val functionId: Int = data("funcid:int").toInt
  val className: String = data("classname")
  val namespace: Span = {
    val strs = data("namespace")
      .split(":")
      .map(str => if (str.nonEmpty) str.toInt else 0)
    Span(
      fileName = fileName,
      begin = SourceLoc(strs(0), strs(1)),
      end = SourceLoc(strs(2), strs(3)))
  }

  val name: String = data("name")
  val endNumber: Int = data("endlineno:int").toInt
  val comment: String = data("doccomment")

  private val nodeTypeStr: String = data("type")
  val node_type: NodeType = NodeType(nodeTypeStr) getOrElse NodeType.NULL

  def is(nType: NodeType): Boolean = nType == node_type

  def isFuncEnd: Boolean = (this is NodeType.CFG_FUNC_EXIT)
  def nonFuncEnd: Boolean = !this.isFuncEnd
  def isBlockEnd: Boolean = (this is NodeType.CFG_BLOCK_EXIT) || (this.isFuncEnd)
  def nonBlockEnd: Boolean = !this.isBlockEnd
  def isConditional: Boolean = (this is NodeType.AST_IF) || (this is NodeType.AST_SWITCH)
  def nonConditional: Boolean = !this.isConditional
  def isFuncEntry: Boolean = (this is NodeType.CFG_FUNC_ENTRY)
  def nonFuncEntry: Boolean = !this.isFuncEntry
  def isNormalNode: Boolean = (this nonBlockEnd) && (this nonConditional) && (this nonFuncEntry)
  def nonNormalNode: Boolean = !this.isNormalNode
}
