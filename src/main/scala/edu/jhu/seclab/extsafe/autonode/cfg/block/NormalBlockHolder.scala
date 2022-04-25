package edu.jhu.seclab.extsafe.autonode.cfg.block

import edu.jhu.seclab.extsafe.autonode.query.csv.model.ModelNode

import scala.collection.mutable.ListBuffer

class NormalBlockHolder extends AbsBlockHolder {

  private val nodesCore = ListBuffer[ModelNode]()
  private val parentsCore = ListBuffer[AbsBlockHolder]()

  override def head: Option[ModelNode] = nodesCore.headOption
  override def last: Option[ModelNode] = nodesCore.lastOption
  override def +=(node: ModelNode): Unit = (this append node)
  override def append(node: ModelNode): NormalBlockHolder = _selfReturn{
    require(this.nonClosed)
    nodesCore += node
  }

  override def parents: Seq[AbsBlockHolder] = this.parentsCore
  override def link(withParent: AbsBlockHolder): NormalBlockHolder = this._selfReturn{ parentsCore += withParent }

  override def nodes: Seq[ModelNode] = nodesCore
}
