package edu.jhu.mssi.seclab.extsafe.cfg.entity

import NodeType.NodeType
import kr.ac.kaist.safe.util.{SourceLoc, Span}

case class Node(
                 private val fileName: String,
                 private val data: Map[String, String]) {

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
  val nodeType: NodeType = NodeType(nodeTypeStr) getOrElse NodeType.NULL

  def is(nType: NodeType): Boolean = nType.toString.toUpperCase() equals nodeTypeStr

  def isBlockEnd: Boolean = (this is NodeType.CFG_BLOCK_EXIT) || (this is NodeType.CFG_FUNC_EXIT)

  def nonBlockEnd: Boolean = !this.isBlockEnd

  def isConditional: Boolean = (this is NodeType.AST_IF) || (this is NodeType.AST_SWITCH)

  def nonConditional: Boolean = !this.isConditional

  def isNormalNode: Boolean = (this nonBlockEnd) && (this nonConditional)

  def nonNormalNode: Boolean = !this.isNormalNode

}
