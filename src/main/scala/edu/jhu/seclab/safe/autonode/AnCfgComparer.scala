package edu.jhu.seclab.safe.autonode

import edu.jhu.seclab.safe.autonode.{query => Querier}
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import kr.ac.kaist.safe.nodes.cfg._

import scala.collection.mutable

class AnCfgComparer(
  val safeCfg: CFG,
  private val autoCfg: AutoNodeCfgHolder
) {

  Querier.sourceOfSafeCfg(safeCfg)

  private def toCalledFuncName(callBlock: Call): String = {
    val innerCall = callBlock.func.getAllBlocks
      .foldLeft(List[CFGInst]()){ (acc, block) => acc ++ block.getInsts }
      .filter(_.isInstanceOf[CFGInternalCall])
      .map(_.asInstanceOf[CFGInternalCall])
      .find(_.lhs.toString == callBlock.callInst.fun.toString())
    innerCall.get.arguments.head.toString()
  }

  private def toCalledFuncName(callBlock: CallBlockHolder): String = {
    val code = callBlock.head.code
    val pattern = """([^\s(]+)\(""".r
    pattern.findFirstMatchIn(code).get.group(1)
  }

  def funcsCompare(): String = {
    val strBuilder = new mutable.StringBuilder("Functions Comparison:\n")
    strBuilder.append(s"  Safe CFG: ${safeCfg.getAllFuncs.map(_.name).mkString(",")} \n\n")
    strBuilder.append(s"  Auto CFG: ${autoCfg.functions.map(_.funcName).mkString(",")} \n\n")
    safeCfg.getAllFuncs.foreach{ safeFunc =>
      val autoFunc =  autoCfg.functions.find(_.funcName == safeFunc.name)
      if(autoFunc isEmpty) {
        strBuilder.append(s"    We do not find out the ${safeFunc.name}\n\n")
      } else {
        strBuilder.append(s"    Comparing calls inside of functions ${safeFunc.name} on both safe and fast\n\n")
        val safeCallFuncNames = Querier.safeCfg.calls(inside=safeFunc.name).map(toCalledFuncName).toSet
        val autoCallFuncNames = autoFunc.get.callBlocks.map(toCalledFuncName).toSet
        strBuilder.append(s"\tInvocations in both sides: ${safeCallFuncNames.intersect(autoCallFuncNames)}\n")
        strBuilder.append(s"\tInvocations in safe only: ${safeCallFuncNames.diff(autoCallFuncNames)}\n")
        strBuilder.append(s"\tInvocations in auto only: ${autoCallFuncNames.diff(safeCallFuncNames)}\n")
      }
    }
    strBuilder.toString()
  }

  def blocksCompare(): String = {
    ""
  }

  def instrsCompare(): String = {
    ""
  }

}