package edu.jhu.seclab.safe.autonode.cfg

import edu.jhu.seclab.safe.autonode.cfg.abs.{AbsBlockHolder, AbsHolder}
import edu.jhu.seclab.safe.autonode.cfg.block.NormBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.safe.autonode.exts.span.Offset
import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.query.autonode.model.{ModelNode, SignatureNode}
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType._
import edu.jhu.seclab.safe.autonode.{query => Querier}

import scala.collection.mutable

class AutoNodeCfgHolder extends AbsHolder {

  private val funcsMap = mutable.Map[String, FunctionHolder]()

  def functions: Seq[FunctionHolder] = funcsMap.values.toSeq

  private def newFunctionHolder(signature: SignatureNode): Unit = {
    val startNode = Querier.autoNode.entryOf(signature)
    if (startNode.isEmpty) throw new Exception(signature.core.toString)
    val entryBlock = new NormBlockHolder().append(startNode.get)
    val newBlock = new NormBlockHolder().flowFrom(entryBlock)
    funcsMap(signature.funcName) = new FunctionHolder(signature).append(entryBlock).append(newBlock)
    newBlockHolder(Querier.autoNode.flowFrom(startNode.get).headOption, signature.funcName, newBlock)
  }

  private def newBlockHolder(from: Option[ModelNode], forFunc: String, curHolder: AbsBlockHolder): Unit = from match {
    case Some(nextStart) if curHolder.isClosed =>
      if (funcsMap(forFunc).exist(_.head == nextStart)) return ;
      val newBlock = new NormBlockHolder().flowFrom(curHolder)
      funcsMap(forFunc).append(newBlock)
      newBlockHolder(from, forFunc, newBlock)
    case Some(entry) if entry isFuncEntry =>
      val signature = Querier.autoNode.signatureOf(entry).get
      if (!funcsMap.contains(signature.funcName)) newFunctionHolder(signature)
      val callerHolder = funcsMap(forFunc).normBlocks.last
      val caller = Querier.autoNode.invocationsInChildrenOf(callerHolder.last)
        .find(call => !funcsMap(forFunc).callBlocks.exists(_.head.id == call.id))
      val preHolder = funcsMap(forFunc).blocks.last
      if (caller.isEmpty) throw new Exception(s"SIGNATURE: ${signature.toString}\n CALLER: ${callerHolder.last}")
      funcsMap(forFunc).append { new CallBlockHolder().append(caller.get).flowFrom(preHolder).close() }
    case Some(funcExit) if funcExit isFuncEnd =>
      if (curHolder.nodes isEmpty) curHolder.append(funcExit).close()
      else funcsMap(forFunc) += new NormBlockHolder().flowFrom(curHolder).append(funcExit).close()
    case Some(node) =>
      curHolder.append(node)
      val (calls, norms) = Querier.autoNode.flowFrom(node).partition(_.isFuncEntry)
      calls.foreach { call => newBlockHolder(call, forFunc, curHolder) }
      if (node.isBlockEnd || node.isConditional) curHolder.close()
      val preHolder = if (calls.isEmpty) curHolder else funcsMap(forFunc).last
      norms.foreach { norm => newBlockHolder(norm, forFunc, preHolder) }
    case None => ()
  }

  Querier.autoNode.fileEntry match {
    case Some(signature) => if (!funcsMap.contains(signature.funcName)) newFunctionHolder(signature)
    case None => println(s"⚠️  Unexpected errors occurred at the begin of functions extraction ⚠️")
  }

  override def toString: String = {
    val sBuilder = new StringBuilder(s"CFG HOLDER: \n")
    this.functions.foreach { fun => sBuilder ++= fun.toString() }
    sBuilder.toString()

  }

}
