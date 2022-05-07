package edu.jhu.seclab.safe.autonode.cfg.block

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.query.csv.model.ModelNode

import scala.collection.mutable.ListBuffer

class NormBlockHolder extends AbsBlockHolder {

  private val nodesCore = ListBuffer[ModelNode]()
  private val parentsCore = ListBuffer[AbsBlockHolder]()

  override def head: ModelNode = nodesCore.head
  override def last: ModelNode = nodesCore.last
  override def +=(node: ModelNode): Unit = (this append node)
  override def append(node: ModelNode): NormBlockHolder = _selfReturn{
    require(this.nonClosed)
    nodesCore += node
  }

  override def parents: Seq[AbsBlockHolder] = this.parentsCore
  override def flowFrom(parent: AbsBlockHolder): NormBlockHolder = this._selfReturn{ parentsCore += parent }

  override def nodes: Seq[ModelNode] = nodesCore
}
