package edu.jhu.seclab.safe.autonode.query.autonode.model

import edu.jhu.seclab.safe.autonode.query.autonode.model.EdgeType.EdgeType
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType.NodeType
import kr.ac.kaist.safe.util.{ SourceLoc, Span }

import scala.util.Try

abstract sealed class AbsEntity

class ModelEdge(val data: Map[String, String]) extends AbsEntity {
  val start: Int = data.getOrElse("start:START_ID", data("start")).toInt
  val end: Int = data.getOrElse("end:END_ID", data("end")).toInt
  val edge_var: String = data.getOrElse("var", "")
  val taint_src: String = data.getOrElse("taint_src", "")
  val taint_dst: String = data.getOrElse("taint_dst", "")
  private val edgeTypeStr: String = data.get("type:TYPE") orElse data.get("type") getOrElse ""
  val edge_type: EdgeType = EdgeType(edgeTypeStr).getOrElse(EdgeType.NULL)

  def is(eType: EdgeType): Boolean = eType == edge_type
  def in(eTypes: EdgeType*): Boolean = eTypes.exists(eTypes => is(eTypes))
}

class ModelNode(
  val fileName: String,
  val data: Map[String, String]) extends AbsEntity {

  val id: Int = (data.get("id:ID") orElse data.get("id") getOrElse "-1").toInt
  val code: String = data.getOrElse("code", "")
  val label: String = data.get("labels:label") orElse data.get("labels") getOrElse ""
  val flags: Array[String] = (data.get("flags:string[]") orElse data.get("flags") getOrElse "").split(" ")
  val lineNumber: Int = Try(data.getOrElse("lineno:int", data("lineno")).toInt).getOrElse(-1)
  val childNumber: Int = Try(data.getOrElse("childnum:int", data("childnum")).toInt).getOrElse(-1)
  val functionId: Int = Try(data.getOrElse("funcid:int", data("funcid")).toInt).getOrElse(-1)
  val className: String = data.getOrElse("classname", "")
  val namespace: Span = {
    var strs = data.getOrElse("namespace", "").split(":").map(str => if (str.nonEmpty) str.toInt else 0)
    if (strs.length != 4) strs = Array(-1, -1, -1, -1)
    Span(
      fileName = fileName,
      begin = SourceLoc(strs(0), strs(1)),
      end = SourceLoc(strs(2), strs(3)))
  }

  val name: String = data.getOrElse("name", "")
  val endNumber: Int = Try(data.getOrElse("endlineno:int", data("endlineno")).toInt).getOrElse(-1)
  val comment: String = data.getOrElse("doccomment", "")

  private val nodeTypeStr: String = data("type")
  val node_type: NodeType = NodeType(nodeTypeStr) getOrElse NodeType.NULL

  def is(nType: NodeType): Boolean = nType.toString.capitalize == node_type.toString.capitalize
  def isNot(nTypes: NodeType*): Boolean = nTypes.forall(nType => !this.is(nType))
  def isInvocation: Boolean = (this is NodeType.AST_CALL) || (this is NodeType.AST_METHOD_CALL)|| (this is NodeType.AST_NEW)
  def nonInvocation: Boolean = !this.isInvocation
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
