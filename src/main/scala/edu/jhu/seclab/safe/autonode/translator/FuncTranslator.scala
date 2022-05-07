package edu.jhu.seclab.safe.autonode.translator

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.safe.autonode.cfg.block.{CallBlockHolder, NormBlockHolder}
import edu.jhu.seclab.safe.autonode.exts.cfg.{BlockLink, FuncCopy}
import edu.jhu.seclab.safe.autonode.exts.syntax.autoWrapToOption
import edu.jhu.seclab.safe.autonode.query.safecfg.SafeCfg
import edu.jhu.seclab.safe.autonode.translator.block.{InvocationTranslator, NormBlockTranslator}
import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGBlock, CFGFunction, Call}

import scala.collection.mutable

class FuncTranslator extends AbsTranslator[CFGFunction] {

  override type InputType = FunctionHolder
  override type DestinationType = CFG

  private var newCfg: CFG = _ // NULLABLE
  private var holder: FunctionHolder = _ // NULLABLE

  override def output(into: CFG): FuncTranslator = _selfReturn{ this.newCfg = into }
  override def input(is: FunctionHolder):FuncTranslator = _selfReturn{ this.holder = is }

  private val translateFromOld: PartialFunction[Option[CFGFunction], Option[CFGFunction]] = { case Some(oldFunc) =>
    val newFunc = newCfg.copyFunctionSignature(from=oldFunc)
    val blocksMap = new mutable.HashMap[AbsBlockHolder, CFGBlock]()
    lazy val normTranslator = new NormBlockTranslator().output(into=newFunc)
    lazy val callTranslator = new InvocationTranslator().output(into=newFunc)
    def createSafeBlock(from: AbsBlockHolder): Unit = {
      val translator = from match {
        case _: NormBlockHolder => normTranslator
        case _: CallBlockHolder => callTranslator
      }
      val safeBlock = translator.input(from).translate()
      if (safeBlock nonEmpty) blocksMap.put(from, safeBlock.get)
    }
    holder.blocks.foreach(createSafeBlock)
    oldFunc.getCaptured.foreach(newFunc.addCaptured)
    def linkSafeBlocks(anHolder: AbsBlockHolder, block: CFGBlock): Unit =
      anHolder.parents.filter(blocksMap.contains).foreach{ parentHolder =>
        var parentBlock =  blocksMap(parentHolder)
        if(parentHolder.isInstanceOf[CallBlockHolder]) parentBlock = parentBlock.asInstanceOf[Call].afterCall
        block.flowFrom(parentBlock)
      }
    blocksMap.foreach{ case(anBlock, safeBlock) => linkSafeBlocks(anBlock, safeBlock) }
    newFunc
  }

  private val createFromNone: PartialFunction[Option[CFGFunction], Option[CFGFunction]] = {
    case None => println(s"CANNOT FIND FUNCTION WITH NAME ${holder.funcName}"); None
  }

  private val translateCore = translateFromOld orElse createFromNone

  override def translate():Option[CFGFunction] = translateCore(
    SafeCfg.query.function(whoseNameIs=holder.funcName)
  )
}
