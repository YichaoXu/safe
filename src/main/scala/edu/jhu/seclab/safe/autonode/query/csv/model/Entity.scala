package edu.jhu.seclab.safe.autonode.query.csv.model

import EdgeType.EdgeType
import NodeType.NodeType
import kr.ac.kaist.safe.util.{SourceLoc, Span}

import scala.util.Try

abstract sealed class AbsEntity

class ModelEdge(val data: Map[String, String]) extends AbsEntity {
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

class ModelNode(
  val fileName: String,
  val data: Map[String, String]
) extends AbsEntity {

  val id: Int = data("id:ID").toInt
  val label: String = data("labels:label")
  val flags: Array[String] = data("flags:string[]").split(" ")
  val lineNumber: Int = Try(data("lineno:int").toInt).getOrElse(-1)
  val code: String = data("code")
  val childNumber: Int = Try(data("childnum:int").toInt).getOrElse(-1)
  val functionId: Int = Try(data("funcid:int").toInt).getOrElse(-1)
  val className: String = data("classname")
  val namespace: Span = {
    var strs = data("namespace").split(":").map(str => if (str.nonEmpty) str.toInt else 0)
    if (strs.length != 4) strs = Array(-1, -1, -1, -1)
    Span(
      fileName = fileName,
      begin = SourceLoc(strs(0), strs(1)),
      end = SourceLoc(strs(2), strs(3))
    )
  }

  val name: String = data("name")
  val endNumber: Int = Try(data("endlineno:int").toInt).getOrElse(-1)
  val comment: String = Try(data("doccomment")).getOrElse("")

  private val nodeTypeStr: String = data("type")
  val node_type: NodeType = NodeType(nodeTypeStr) getOrElse NodeType.NULL

  def is(nType: NodeType): Boolean = nType == node_type
  def isNot(nTypes: NodeType*): Boolean = nTypes.forall(nType=> !this.is(nType))
  def isInvocation:Boolean = (this is NodeType.AST_CALL) || (this is NodeType.AST_METHOD_CALL)
  def nonInvocation:Boolean = !this.isInvocation
  def isFuncEnd: Boolean = (this is NodeType.CFG_FUNC_EXIT)
  def nonFuncEnd: Boolean = !this.isFuncEnd
  def isBlockEnd: Boolean = (this is NodeType.CFG_BLOCK_EXIT)
  def nonBlockEnd: Boolean = !this.isBlockEnd
  def isConditional: Boolean = (this is NodeType.AST_IF) || (this is NodeType.AST_SWITCH_LIST)
  def nonConditional: Boolean = !this.isConditional
  def isFuncEntry: Boolean = (this is NodeType.CFG_FUNC_ENTRY)
  def nonFuncEntry: Boolean = !this.isFuncEntry
  def isNormalNode: Boolean = (this nonBlockEnd) && (this nonConditional) && (this nonFuncEntry) && (this nonFuncEnd)
  def nonNormalNode: Boolean = !this.isNormalNode

  override def toString: String =
    s"{id: ${id}; \trange: ${namespace.begin}-${namespace.end}; \ttype: ${node_type}; \tcode: ${code};}"
}
