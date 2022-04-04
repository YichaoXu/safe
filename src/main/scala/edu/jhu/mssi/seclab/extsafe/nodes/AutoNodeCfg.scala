package edu.jhu.mssi.seclab.extsafe.nodes

import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock, CFGEdgeType, CFGFunction, CFGId }
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.Span

import scala.collection.mutable
import scala.reflect.io.Path

class AutoNodeCfg(
  private val delegated: CFG,
  private val nodesCsvPath: String,
  private val edgesCsvPath: String) extends CFG(delegated.ir, delegated.globalVars) {

  override lazy val globalFunc: CFGFunction = delegated.globalFunc

  private val nodesSet = getSetFromCsv(nodesCsvPath)
  private val edgesSet = getSetFromCsv(edgesCsvPath)

  private def getSetFromCsv(filePath: String): mutable.Map[String, Map[String, String]] = {
    val output = mutable.Map[String, Map[String, String]]()
    val nodesCsv = io.Source.fromFile(nodesCsvPath)
    val fileRows = nodesCsv.getLines()
    val fileHeader = fileRows.next().split("\t")
    fileRows
      .map(string => (string, string.split("\t").toList))
      .filter(_._2(fileHeader.indexOf("namespace")).nonEmpty)
      .foreach(tuple => Map[String, String]() ++ fileHeader.zip(tuple._2))
    nodesCsv.close()
    output
  }

  private def findNode(span: Span): Option[(String, Map[String, String])] = {
    nodesSet.find {
      case (key: String, value: Map[String, String]) =>
        val autoNodeSpan = value("namespace").split(":")
        val safeBeg = span.begin.toString.split(":")
        val safeEnd = span.end.toString.split(":")
        val autoBeg = Seq(autoNodeSpan(0), autoNodeSpan(1))
        val autoEnd = Seq(autoNodeSpan(2), autoNodeSpan(3))
        if (autoBeg(1).toInt > safeBeg(1).toInt) return Option.empty
        if (autoBeg(2).toInt > safeBeg(2).toInt) return Option.empty
        if (autoEnd(1).toInt < safeEnd(1).toInt) return Option.empty
        if (autoEnd(2).toInt < safeEnd(2).toInt) return Option.empty
        //TODO: calculate the value
        true
    }
  }

  private def isNodeExists(span: Span): Boolean =
    findNode(span).nonEmpty

  private def isEdgeExists(from: CFGBlock, to: CFGBlock): Boolean = {
    findNode(from.span).nonEmpty && findNode(to.span).nonEmpty

  }

  override def createFunction(
    argumentsName: String, argVars: List[CFGId], localVars: List[CFGId],
    name: String, ir: IRNode, isUser: Boolean): CFGFunction = if (isNodeExists(ir.span))
    super.createFunction(argumentsName, argVars, localVars, name, ir, isUser)
  else
    CFGFunction(ir, argumentsName, argVars, localVars, name, isUser)

  override def addJSModel(func: CFGFunction): Unit = if (isNodeExists(ir.span)) super.addJSModel(func)

  override def addEdge(
    fromList: List[CFGBlock], toList: List[CFGBlock],
    etype: CFGEdgeType): Unit = for (from <- fromList; to <- toList) {
    if (!isEdgeExists(from, to)) return
    from.addSucc(etype, to)
    to.addPred(etype, from)
  }
}
