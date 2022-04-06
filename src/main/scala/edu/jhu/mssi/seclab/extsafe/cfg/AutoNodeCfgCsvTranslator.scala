package edu.jhu.mssi.seclab.extsafe.cfg

import edu.jhu.mssi.seclab.extsafe.cfg.builder.{AbsBuilder, FakeIRNodeBuilder, FunctionBuilder}
import edu.jhu.mssi.seclab.extsafe.cfg.entity.{Edge, EdgeType, Node, NodeType}
import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGAlloc, CFGBlock, CFGFunction, CFGId, NoLabel, NormalBlock}
import edu.jhu.mssi.seclab.extsafe.utils.Syntax.with_csv

import java.io.File

class AutoNodeCfgCsvTranslator(
  private val oldCfg: CFG,
  private val nodesCsv: File,
  private val edgesCsv: File
) extends AbsBuilder {

  private val irFactory = new FakeIRNodeBuilder()
  private val flowEdges = with_csv(edgesCsv){ eReader =>
    eReader.toStreamWithHeaders
      .map(data => Edge(data))
      .filter(edge => edge is EdgeType.FLOWS_TO)
      .toList
  }
  private val flowNodes = with_csv(nodesCsv){ nReader =>
    nReader.toStreamWithHeaders
      .map(data => Node(nodesCsv.getName, data))
      .filter{ node => flowEdges.exists(e => e.start == node.id || e.end == node.id)}
      .toList
  }
  private val newCfg = new CFG(oldCfg.ir, oldCfg.globalVars)
  private var funcsMap: Map[String, CFGFunction] = Map()

  def build(): CFG = {
    val topLevelNode = flowNodes.find(node=> (node is NodeType.AST_TOP_LEVEL) && node.id != 1)
    buildFuncsFrom(topLevelNode.get)
    newCfg
  }

  private def buildFuncsFrom(node: Node): Unit = {
    val oldFunc = {
      if (node is NodeType.AST_TOP_LEVEL) Some(oldCfg.globalFunc)
      else oldCfg.getAllFuncs.find(_.name == node.code)
    }
    require(oldFunc nonEmpty)
    oldFunc.get.argVars
  }

  private def buildBlocksFrom(node: Node, newFunc: CFGFunction, fBuilder: FunctionBuilder): List[CFGBlock] = node match {
    case eNode if eNode.isBlockEnd => List()
    case nNode if nNode.isNormalNode =>
      val newBlock = newFunc.createBlock(NoLabel, None)
      val allocVar = nNode.code.split("=")(0).trim()
      newBlock.createInst(nBlock => nNode.nodeType match {
        case NodeType.AST_ASSIGN => CFGAlloc(
          ir = irFactory.span(nNode.namespace).build(),
          block = nBlock, lhs = oldFunc.getAllBlocks.find()
        )

      })

      ???
    case _ => List()
  }
}


}
