package edu.jhu.mssi.seclab.extsafe.autonode.csv.query

import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.ModelNode
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.NodeType.NodeType

import java.io.File

class NodeCsv(jFile: File) extends AbsCsvQuerier[ModelNode](jFile: File) {

  override val all: Stream[ModelNode] = with_csv { nReader =>
    nReader.toStreamWithHeaders.map(data => ModelNode(fileName = jFile.getName, data))
  }

  def byIndex(i: Int): Option[ModelNode] = {
    this.nodes.lift(i)
  }

  def byId(i: Int): Option[ModelNode] = {
    val index = i - this.nodes.head.id
    this.byIndex(index)
  }

  def nodes: Stream[ModelNode] = this.all
  def nodes(ofType: NodeType): Stream[ModelNode] = this.any(node => node is ofType)

}

object NodeCsv {
  private var core: Option[NodeCsv] = None
  def init(eFile: File): Unit = {
    core = Some(new NodeCsv(eFile))
  }
  lazy val query: NodeCsv = core.get
}