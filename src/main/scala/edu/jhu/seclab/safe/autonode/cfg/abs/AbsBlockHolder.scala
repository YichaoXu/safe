package edu.jhu.seclab.safe.autonode.cfg.abs

import kr.ac.kaist.safe.util.Span
import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import edu.jhu.seclab.safe.autonode.query.autonode.model.ModelNode
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType._
import kr.ac.kaist.safe.nodes.cfg.{ CFGFunExpr, CFGInst, CFGNoOp }

abstract class AbsBlockHolder extends AbsMutableHolder[ModelNode] {
  def parents: Seq[AbsBlockHolder]
  def flowFrom(parent: AbsBlockHolder): AbsBlockHolder
  def nodes: Seq[ModelNode]

  def span: Span = nodes.filter(n => (n.namespace.begin.line >= 0) && (n isNot AST_STMT_LIST)) match {
    case valid if valid nonEmpty =>
      val begin = valid.minBy { node => (node.namespace.end.line, node.namespace.end.column) }
      val end = valid.maxBy { node => (node.namespace.end.line, node.namespace.end.column) }
      begin.namespace + end.namespace
    case _ => Span()
  }

  def shouldContain(inst: CFGInst): Boolean = inst match {
    case _: CFGNoOp =>
      false
    case funcDefInst: CFGFunExpr =>
      nodes.filter(_ is AST_FUNC_DECL).exists { _.code == funcDefInst.lhs.text }
    case inst: CFGInst =>
      !nodes.filter(_ is AST_FUNC_DECL).exists { _.namespace.isCrossover(inst.span) } &&
        nodes.filter(_ isNot (AST_FUNC_DECL, AST_STMT_LIST)).exists { _.namespace.isCrossover(inst.span) }
  }

  override def close(): AbsBlockHolder = _selfReturn(super.close())

  override def toString: String = {
    val sBuilder = new StringBuilder()
      .append(s"\t${this.getClass.getSimpleName}: ${this.hashCode()}\t")
      .append(s"Namespace: ${this.span}\n")
    parents.foreach(block => sBuilder ++= s"\t\tPARENT: ${block.hashCode()}\n")
    nodes.foreach(node => sBuilder ++= s"\t\tNODE: ${node.toString}\n")

    sBuilder.toString()
  }
}