package edu.jhu.seclab.safe.autonode.cfg

import edu.jhu.seclab.safe.autonode.cfg.abs.{AbsBlockHolder, AbsHolder}
import edu.jhu.seclab.safe.autonode.cfg.block.NormBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.safe.autonode.query.csv.AutoNodeCsv
import edu.jhu.seclab.safe.autonode.query.csv.model.ModelNode
import edu.jhu.seclab.safe.autonode.query.csv.model.NodeType.AST_TOP_LEVEL

import java.io.File
import scala.collection.mutable

class AutoNodeCfgHolder(
  private val nodesCsv: File,
  private val edgesCsv: File
) extends AbsHolder {

  AutoNodeCsv.init(nodesCsv, edgesCsv)

  private val funcsMap = mutable.Map[String, FunctionHolder]()

  def functions: Seq[FunctionHolder] = funcsMap.values.toSeq

  private def newFunctionHolder(funcId: Int): Unit = AutoNodeCsv.query.node(id = funcId) match {
    case Some(funcDef) =>
      val funcName = funcDef.node_type match {
        case AST_TOP_LEVEL => "top-level"
        case _ => funcDef.code
      }
      val startNode = AutoNodeCsv.query.node(id = funcId + 1).get
      val entryBlock = new NormBlockHolder().append(startNode)
      val newBlock = new NormBlockHolder().flowFrom(entryBlock)
      val newFunc = new FunctionHolder(funcDef).append(entryBlock).append(newBlock)
      funcsMap(funcName) = newFunc
      newBlockHolder(AutoNodeCsv.query.next(of = startNode).headOption, funcName, newBlock)
    case None => print(s"CANNOT FIND FUNCTION WITH ID-$funcId")
  }

  private def newBlockHolder(from: Option[ModelNode], forFunc: String, curHolder: AbsBlockHolder): Unit = from match {
    case Some(nextStart) if curHolder.isClosed =>
      if (funcsMap(forFunc).exist(_.head == nextStart)) return ;
      funcsMap(forFunc) += new NormBlockHolder().flowFrom(curHolder)
      newBlockHolder(from, forFunc, funcsMap(forFunc).last)
    case Some(funcNode) if funcNode isFuncEntry =>
      if (!funcsMap.contains(funcNode.code)) newFunctionHolder(funcNode.id - 1)
      val callNode = curHolder.last
      val callHolder = new CallBlockHolder().append(callNode).flowFrom(curHolder).close()
      funcsMap(forFunc) += callHolder
      newBlockHolder(AutoNodeCsv.query.next(callNode).lift(1), forFunc, callHolder)
    case Some(funcExit) if funcExit isFuncEnd =>
      if(curHolder.nodes isEmpty) curHolder.append(funcExit).close()
      else funcsMap(forFunc) += new NormBlockHolder().flowFrom(curHolder).append(funcExit).close()
    case Some(condNode) if condNode isConditional =>
      curHolder.append(condNode).close()
      AutoNodeCsv.query.next(condNode).foreach { node => newBlockHolder(Some(node), forFunc, curHolder) }
    case Some(blockEnd) if blockEnd isBlockEnd =>
      curHolder.append(blockEnd).close()
      newBlockHolder(AutoNodeCsv.query.next(blockEnd).headOption, forFunc, curHolder)
    case Some(normalNode) =>
      curHolder.append(normalNode)
      if (normalNode.isBlockEnd) curHolder.close()
      newBlockHolder(AutoNodeCsv.query.next(normalNode).headOption, forFunc, curHolder)
    case None => ()
  }
  AutoNodeCsv.query.nodes.find(funDel => funDel.is(AST_TOP_LEVEL) && funDel.id != 1) match {
    case Some(node) => if (!funcsMap.contains(node.code)) newFunctionHolder(node.id)
  }

  override def toString: String = {
    val sBuilder = new StringBuilder(s"CFG HOLDER: \n")
    this.functions.foreach { fun => sBuilder ++= fun.toString() }
    sBuilder.toString()
  }

}
