package edu.jhu.mssi.seclab.extsafe.autonode.csv.query

import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.EdgeType.{ EdgeType, FLOWS_TO }
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.{ ModelEdge, ModelNode }

import java.io.File

private class EdgeCsv(eFile: File) extends AbsCsvQuerier[ModelEdge](eFile) {

  lazy override val all: Stream[ModelEdge] = with_csv { eReader =>
    eReader.toStreamWithHeaders.map(data => ModelEdge(data))
  }

  def edges: Stream[ModelEdge] = this.all
  def edges(ofType: EdgeType): Stream[ModelEdge] = this.any(edge => edge is ofType)

  def from(node: ModelNode)(whoseType: EdgeType = FLOWS_TO): Stream[ModelEdge] =
    this.edges(whoseType).filter(each => each.start == node.id)
  def to(node: ModelNode)(whoseType: EdgeType = FLOWS_TO): Stream[ModelEdge] =
    this.edges(whoseType).filter(each => each.end == node.id)
}

object EdgeCsv {
  private var core: Option[EdgeCsv] = None
  def init(eFile: File): Unit = {
    core = Some(new EdgeCsv(eFile))
  }
  lazy val query: EdgeCsv = core.get
}
