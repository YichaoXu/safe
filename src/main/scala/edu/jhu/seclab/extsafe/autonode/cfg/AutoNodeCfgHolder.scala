package edu.jhu.seclab.extsafe.autonode.cfg

import edu.jhu.seclab.extsafe.autonode.cfg.abs.AbsHolder
import edu.jhu.seclab.extsafe.autonode.cfg.block.NormalBlockHolder
import edu.jhu.seclab.extsafe.autonode.cfg.block.{AbsBlockHolder, CallBlockHolder}
import edu.jhu.seclab.extsafe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.extsafe.autonode.query.csv.AutoNodeCsv
import edu.jhu.seclab.extsafe.autonode.query.csv.model.ModelNode
import edu.jhu.seclab.extsafe.autonode.query.csv.model.NodeType.AST_TOP_LEVEL

import java.io.File
import scala.collection.mutable

class AutoNodeCfgHolder(
  private val nodesCsv: File,
  private val edgesCsv: File) extends AbsHolder {

  AutoNodeCsv.init(nodesCsv, edgesCsv)

  private val funcsMap = mutable.Map[String, FunctionHolder]()

  def functions: Seq[FunctionHolder] = funcsMap.values.toSeq

  private def collectFuncsFromIfNotExist(funcId: Int): Unit = AutoNodeCsv.query.node(id = funcId) match {
    case Some(funcDef) =>
      val funcName = funcDef.node_type match {
        case AST_TOP_LEVEL => "top-level"
        case _ => funcDef.code
      }
      if (funcsMap.contains(funcName)) return ;
      val startNode = AutoNodeCsv.query.node(id = funcId + 1).get
      val newBlock = new NormalBlockHolder().append(startNode)
      val newFunc = new FunctionHolder(funcDef).append(newBlock)
      funcsMap(funcName) = newFunc
      iterativelyCreateAllHolders(AutoNodeCsv.query.next(newBlock.nodes.last).headOption, funcName, newBlock)
    case None => print(s"CANNOT FIND FUNCTION WITH ID-$funcId")
  }

  private def iterativelyCreateAllHolders(from: Option[ModelNode], forFunc: String, curHolder: AbsBlockHolder): Unit =
    from match {
      case Some(nextStart) if curHolder.isClosed =>
        if (funcsMap(forFunc).exist(_.head.get == nextStart)) return ;
        funcsMap(forFunc) += new NormalBlockHolder().link(curHolder)
        iterativelyCreateAllHolders(from, forFunc, funcsMap(forFunc).last.get)
      case Some(funcNode) if funcNode isFuncEntry =>
        collectFuncsFromIfNotExist(funcNode.id - 1)
        val callNode = curHolder.last.get
        val callHolder = new CallBlockHolder().append(callNode).link(curHolder).close()
        funcsMap(forFunc) += callHolder
        iterativelyCreateAllHolders(AutoNodeCsv.query.next(callNode).lift(1), forFunc, callHolder)
      case Some(funcExit) if funcExit isFuncEnd =>
        curHolder.append(funcExit).close()
      case Some(condNode) if condNode isConditional =>
        curHolder.append(condNode).close()
        AutoNodeCsv.query.next(condNode).foreach { node => iterativelyCreateAllHolders(Some(node), forFunc, curHolder) }
      case Some(blockEnd) if blockEnd isBlockEnd=>
        curHolder.append(blockEnd).close()
        iterativelyCreateAllHolders(AutoNodeCsv.query.next(blockEnd).headOption, forFunc, curHolder)
      case Some(normalNode) =>
        curHolder.append(normalNode)
        if (normalNode.isBlockEnd) curHolder.close()
        iterativelyCreateAllHolders(AutoNodeCsv.query.next(normalNode).headOption, forFunc, curHolder)
      case None => ()
    }
  AutoNodeCsv.query.nodes.find(funDel => funDel.is(AST_TOP_LEVEL) && funDel.id != 1) match {
    case Some(node) => collectFuncsFromIfNotExist(node.id)
  }

  override def toString: String = {
    val sBuilder = new StringBuilder(s"CFG HOLDER: \n")
    this.functions.foreach { fun => sBuilder ++= fun.toString() }
    sBuilder.toString()
  }

}
