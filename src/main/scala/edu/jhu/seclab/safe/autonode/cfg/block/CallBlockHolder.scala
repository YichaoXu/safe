package edu.jhu.seclab.safe.autonode.cfg.block

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.query.csv.model.ModelNode

class CallBlockHolder extends AbsBlockHolder {

  private var parent: Option[AbsBlockHolder] = None

  override def parents: Seq[AbsBlockHolder] = parent match {
    case Some(call) => Seq(call)
    case _ => Nil
  }

  override def flowFrom(parent: AbsBlockHolder): CallBlockHolder = this._selfReturn {
    require(this.parent isEmpty)
    this.parent = Some(parent)
  }

  private var nodeCore: Option[ModelNode] = None
  override def head: ModelNode = nodeCore.get
  override def last: ModelNode = nodeCore.get


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