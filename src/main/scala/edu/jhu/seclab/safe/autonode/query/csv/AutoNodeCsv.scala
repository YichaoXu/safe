package edu.jhu.seclab.safe.autonode.query.csv

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import edu.jhu.seclab.safe.autonode.query.csv.model.EdgeType.{ENTRY, EdgeType, FLOWS_TO, PARENT_OF}
import edu.jhu.seclab.safe.autonode.query.csv.model.{ModelEdge, ModelNode}
import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.query.csv.model.NodeType.{AST_CLOSURE, STRING}

import java.io.File

class AutoNodeCsv(nodeFile: File, edgeFile: File) {

  implicit val format: DefaultCSVFormat = new DefaultCSVFormat {
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

  def flowFrom(target: ModelNode): Seq[ModelNode] = this.next(of=target, eType=FLOWS_TO)
  def flowTo(target: ModelNode): Seq[ModelNode] = this.prev(of=target, eType=FLOWS_TO)

  def signatureOf(entry: ModelNode): Option[ModelNode] = this.prev(of=entry, eType=ENTRY).headOption match {
    case Some(signature) if signature.is(AST_CLOSURE) && signature.code.isEmpty =>
      this.next(of=signature, PARENT_OF).find(_ is STRING).orElse(
        Range.inclusive(signature.id-5, signature.id+5).map(id=>this.node(id).get).find(_ is STRING)
      ) match {
        case Some(strNode) => new ModelNode(signature.fileName, signature.data.updated("code", strNode.name))
        case None => println(s"⚠️  CANNOT FOUND THE SIGNATURE NAME OF ${signature.toString} ⚠️")
      }
    case Some(signature) => signature
    case None => println(s"⚠️  CANNOT FOUND THE SIGNATURE OF ENTRY ${entry.toString} ⚠️")
  }

  def entryOf(signature: ModelNode): Option[ModelNode] = this.next(of=signature, eType=ENTRY).headOption

  def invocationsInChildrenOf(target: ModelNode): Seq[ModelNode] = {
    val children = this.next(of=target, PARENT_OF).flatMap(invocationsInChildrenOf)
    if(target nonInvocation) children
    else children :+ target
  }

  def next(of: ModelNode, eType: Option[EdgeType] = None): Seq[ModelNode] =
    edges.filter{edge => edge.start == of.id && (eType.isEmpty || edge.is(eType.get))}.map{edge => node(id=edge.end).get}
  def prev(of: ModelNode, eType: Option[EdgeType] = None): Seq[ModelNode] =
    edges.filter(edge => edge.end == of.id && (eType.isEmpty || edge.is(eType.get))).map{edge => node(id=edge.start).get}
}

object AutoNodeCsv {

  private var core: Option[AutoNodeCsv] = None

  def init(nFile: File, eFile: File): Unit = {
    core = Some(new AutoNodeCsv(nodeFile=nFile, edgeFile=eFile))
  }
  lazy val query: AutoNodeCsv = core.get
}
