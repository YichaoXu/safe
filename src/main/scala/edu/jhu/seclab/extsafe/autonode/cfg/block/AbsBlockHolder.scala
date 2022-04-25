package edu.jhu.seclab.extsafe.autonode.cfg.block

import edu.jhu.seclab.extsafe.autonode.cfg.abs.AbsMutableHolder
import edu.jhu.seclab.extsafe.autonode.query.csv.model.ModelNode
import edu.jhu.seclab.extsafe.autonode.query.csv.model.NodeType.{AST_CATCH, AST_CATCH_LIST, AST_FUNC_DECL, AST_IF, AST_IF_ELEM, AST_STMT_LIST, AST_SWITCH_CASE, AST_SWITCH_LIST, AST_TRY, CFG_FUNC_ENTRY, CFG_FUNC_EXIT}
import kr.ac.kaist.safe.nodes.cfg.CFGInst
import kr.ac.kaist.safe.util.Span

abstract class AbsBlockHolder extends AbsMutableHolder[ModelNode] {
  def parents: Seq[AbsBlockHolder]
  def link(withParent: AbsBlockHolder): AbsBlockHolder
  def nodes: Seq[ModelNode]

  private var __namespace: Option[Span] = None
  def instrSpan: Span = nodes
    .filter(n=> n.namespace.begin.line>=0)
    .filter(_.isNot(CFG_FUNC_ENTRY, CFG_FUNC_EXIT, AST_STMT_LIST, AST_FUNC_DECL))
    .filter(_.isNot(AST_IF, AST_IF_ELEM, AST_SWITCH_LIST, AST_SWITCH_CASE))
    .filter(_.isNot(AST_TRY, AST_CATCH_LIST, AST_CATCH))
    match {
      case valid if valid nonEmpty =>
        val begin = valid.minBy{node=> (node.namespace.end.line, node.namespace.end.column)}
        val end = valid.maxBy{node=> (node.namespace.end.line, node.namespace.end.column)}
        __namespace = Some(begin.namespace + end.namespace)
        __namespace.get
      case _ =>
        Span()
    }

  def shouldContain(inst: CFGInst): Boolean = inst match {
    case
    case Span(_, bBegin, bEnd) => bBegin <= space.begin && bEnd >= space.end
  }

  override def close(): AbsBlockHolder = _selfReturn(super.close())

  override def toString: String = {
    val sBuilder = new StringBuilder(s"\tBLOCK: ${this.hashCode()}\tNamespace: ${this.instrSpan}\n")
    parents.foreach(block => sBuilder ++= s"\t\tPARENT: ${block.hashCode()}\n")
    nodes.foreach(node => sBuilder ++= s"\t\tNODE: ${node.toString}\n")

    sBuilder.toString()
  }
}