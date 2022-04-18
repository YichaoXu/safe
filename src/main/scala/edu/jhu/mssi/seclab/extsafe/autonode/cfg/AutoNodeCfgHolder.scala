package edu.jhu.mssi.seclab.extsafe.autonode.cfg

import edu.jhu.mssi.seclab.extsafe.autonode.cfg.block.{ AbsBlockHolder, CallBlockHolder, NormalBlockHolder }
import edu.jhu.mssi.seclab.extsafe.autonode.cfg.function.FunctionHolder
import edu.jhu.mssi.seclab.extsafe.autonode.csv.query.{ EdgeCsv, NodeCsv }
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.EdgeType.FLOWS_TO
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.NodeType.AST_TOP_LEVEL

import java.io.File
import scala.collection.mutable

class AutoNodeCfgHolder(
  private val nodesCsv: File,
  private val edgesCsv: File) extends AbsHolder {
  EdgeCsv.init(edgesCsv); NodeCsv.init(nodesCsv)

  private val funcsMap = mutable.Map[String, FunctionHolder]()

  def functions: Seq[FunctionHolder] = funcsMap.values.toSeq

  NodeCsv.query.nodes.find(funDel => funDel.is(AST_TOP_LEVEL) && funDel.id != 2) match {
    case Some(node) => collectFuncsFromIfNotExist(node.id)
  }

  private def collectFuncsFromIfNotExist(funcDefIndex: Int): Unit = NodeCsv.query.byId(funcDefIndex) match {
    case Some(funcDef) if !funcsMap.contains(funcDef.code) =>
      funcsMap(funcDef.code) = new FunctionHolder(funcDef)
      val funEntry = NodeCsv.query.byId(funcDefIndex + 1).get
      val newBlock = new NormalBlockHolder() :+= funEntry
      funcsMap(funcDef.code) :+= newBlock
      iterativelyCreateAllHolders(funEntry.id + 2, funcDef.code, newBlock)
  }

  private def iterativelyCreateAllHolders(fromId: Int, forFunc: String, curHolder: AbsBlockHolder): Unit =
    NodeCsv.query.byId(fromId) match {
      case Some(node) if curHolder.isClosed && funcsMap(forFunc).exist(_.head.get == node) =>
        val newBlock = new NormalBlockHolder().link(curHolder)
        funcsMap(forFunc) += newBlock
        iterativelyCreateAllHolders(fromId, forFunc, newBlock)
      case Some(node) if node.isNormalNode || node.isBlockEnd =>
        curHolder += node
        if (node.isBlockEnd) curHolder.close()
        EdgeCsv.query.from(node)(FLOWS_TO).headOption match {
          case Some(edge) =>
            iterativelyCreateAllHolders(edge.end, forFunc, curHolder)
        }
      case Some(node) if node isFuncEntry =>
        collectFuncsFromIfNotExist(node.id - 1)
        val callNode = curHolder.tail.get
        val callHolder = new CallBlockHolder()
        callHolder += callNode
        callHolder.link(withParent = curHolder).close()
        funcsMap(forFunc) += callHolder
        val afterCallEdge = EdgeCsv.query.from(callNode)(FLOWS_TO)(1)
        iterativelyCreateAllHolders(afterCallEdge.end, forFunc, callHolder)
      case Some(node) if node isConditional =>
        curHolder += node
        curHolder.close()
        EdgeCsv.query.from(node)(FLOWS_TO).foreach { edge =>
          iterativelyCreateAllHolders(edge.end, forFunc, curHolder)
        }
    }
}
