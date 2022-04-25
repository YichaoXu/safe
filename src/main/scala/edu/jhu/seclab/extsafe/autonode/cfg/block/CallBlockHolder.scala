package edu.jhu.seclab.extsafe.autonode.cfg.block

import edu.jhu.seclab.extsafe.autonode.query.csv.model.ModelNode

class CallBlockHolder extends AbsBlockHolder {

  private var parent: Option[AbsBlockHolder] = None

  override def parents: Seq[AbsBlockHolder] = parent match {
    case Some(call) => Seq(call)
    case _ => Nil
  }

  override def link(withParent: AbsBlockHolder): CallBlockHolder = this._selfReturn {
    require(parent isEmpty)
    parent = Some(withParent)
  }

  private var nodeCore: Option[ModelNode] = None
  override def head: Option[ModelNode] = nodeCore
  override def last: Option[ModelNode] = nodeCore

  override def +=(node: ModelNode): Unit = (this append node)
  override def append(node: ModelNode): CallBlockHolder = _selfReturn {
    require(this.nodeCore.isEmpty && this.nonClosed)
    this.nodeCore = Some(node)
  }

  override def nodes: Seq[ModelNode] = nodeCore match {
    case Some(node) => Seq(node)
    case _ => Nil
  }
}