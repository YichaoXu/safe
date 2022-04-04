package edu.jhu.mssi.seclab.extsafe.cfg

import edu.jhu.mssi.seclab.extsafe.cfg.builder.AbstractBuilder
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGFunction }
import edu.jhu.mssi.seclab.extsafe.csv.entity.{ Edge, EdgeType, Node, NodeType }
import edu.jhu.mssi.seclab.extsafe.utils.FakeIRNodeFactory
import edu.jhu.mssi.seclab.extsafe.utils.Syntax.{with_csv, with_multi_csv}
import edu.jhu.mssi.seclab.extsafe.utils.File.lines

import java.io.File

class AutoNodeCfgCsvTranslator(
  private val srcJs: File,
  private val nodesCsv: File,
  private val edgesCsv: File
) extends AbstractBuilder[CFG] {

  private val irFactory = new FakeIRNodeFactory()

  private val cfgCore = {
    val cfgIr = irFactory.fileName(srcJs.getName).end(lines(srcJs)).build()
    new CFG(cfgIr, List())
  }

  override def build(): Option[CFG] = {
    val flowEdges = with_csv(edgesCsv){ eReader =>
      eReader.toStreamWithHeaders
        .map(data => Edge(data))
        .filter(edge => edge is EdgeType.FLOWS_TO)
    }
    val flowNodes = with_csv(nodesCsv){ nReader =>
      nReader.toStreamWithHeaders
        .map(data => Node(nodesCsv.getName, data))
        .filter{ node => flowEdges.exists(e => e.start == node.id || e.end == node.id)}
    }

    def buildFunction(nodeIndex: Int) = {
      val node = flowNodes(nodeIndex)
      val edgeIndex = flowEdges.indexWhere(_.start == node.id)
      while(edgeIndex > 0) {
        val edge = flowNodes(edgeIndex)
        cfgCore.createFunction()
      }
    }
    flowEdges.head

    ???
  }




}
