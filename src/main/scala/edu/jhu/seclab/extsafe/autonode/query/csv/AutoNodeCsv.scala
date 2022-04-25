package edu.jhu.seclab.extsafe.autonode.query.csv

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import edu.jhu.seclab.extsafe.autonode.query.csv.model.EdgeType.{EdgeType, FLOWS_TO}
import edu.jhu.seclab.extsafe.autonode.query.csv.model.{ModelEdge, ModelNode}

import java.io.File

class AutoNodeCsv(nodeFile: File, edgeFile: File) {

  implicit val format: DefaultCSVFormat = new DefaultCSVFormat {
    override val escapeChar: Char = '\\'
    override val delimiter = '\t'
  }

  private def with_csv[Output](csvFile: File)(behavior: CSVReader => Output): Output = {
    val reader = CSVReader.open(csvFile)
    val output = behavior(reader)
    reader.close()
    output
  }

  lazy val edges: Seq[ModelEdge] = with_csv(edgeFile) { nReader =>
    nReader.toStreamWithHeaders.map(data => new ModelEdge(data)).toList
  }

  lazy val nodes: Seq[ModelNode] = with_csv(nodeFile) { eReader =>
    eReader.toStreamWithHeaders.map(data => new ModelNode(fileName = nodeFile.getName, data)).toList
  }

  def node(id: Int): Option[ModelNode] = nodes.find(_.id == id)

  def next(ofNode: ModelNode, edgeType: EdgeType = FLOWS_TO): Seq[ModelNode] =
    edges.filter(edge => edge.is(edgeType) && edge.start == ofNode.id).map{ edge => node(id=edge.end).get }

  def prev(ofNode: ModelNode, edgeType: EdgeType = FLOWS_TO): Seq[ModelNode] =
    edges.filter(edge => edge.is(edgeType) && edge.end == ofNode.id).map{ edge => node(id=edge.start).get }

}

object AutoNodeCsv {

  private var core: Option[AutoNodeCsv] = None

  def init(nFile: File, eFile: File): Unit = {
    core = Some(new AutoNodeCsv(nodeFile=nFile, edgeFile=eFile))
  }
  lazy val query: AutoNodeCsv = core.get
}
