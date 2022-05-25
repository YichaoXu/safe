package edu.jhu.seclab.safe.autonode.query.autonode

import com.github.tototoshi.csv.{ CSVReader, TSVFormat }
import edu.jhu.seclab.safe.autonode.query.autonode.model.EdgeType._
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType.AST_TOP_LEVEL
import edu.jhu.seclab.safe.autonode.query.autonode.model.{ ModelEdge, ModelNode }

import java.io.File

class AutoNodeCsv(nodeFile: File, edgeFile: File) extends AbsAutoNode {

  implicit val format: TSVFormat = new TSVFormat {}

  private def with_csv[Output](csvFile: File)(behavior: CSVReader => Output): Output = {
    val reader = CSVReader.open(csvFile)
    val output = behavior(reader)
    reader.close()
    output
  }

  private lazy val edges: Seq[ModelEdge] = with_csv(edgeFile) { nReader =>
    nReader.toStreamWithHeaders.map(data => new ModelEdge(data)).toList
  }
  private lazy val nodes: Seq[ModelNode] = with_csv(nodeFile) { eReader =>
    eReader.toStreamWithHeaders.map(data => new ModelNode(fileName = nodeFile.getName, data)).toList
  }

  override def fileEntry: Option[ModelNode] = nodes.find(funDel => funDel.is(AST_TOP_LEVEL) && funDel.id != 1)

  override def next(of: ModelNode, eType: Option[EdgeType] = None): Seq[ModelNode] = edges
    .filter { edge => edge.start == of.id && (eType.isEmpty || edge.is(eType.get)) }
    .map { edge => node(id = edge.end).get }
  override def prev(of: ModelNode, eType: Option[EdgeType] = None): Seq[ModelNode] = edges
    .filter(edge => edge.end == of.id && (eType.isEmpty || edge.is(eType.get)))
    .map { edge => node(id = edge.start).get }

  override def node(id: Int): Option[ModelNode] = nodes.find(_.id == id)
}
