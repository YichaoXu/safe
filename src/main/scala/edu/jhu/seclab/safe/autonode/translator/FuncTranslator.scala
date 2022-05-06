package edu.jhu.seclab.safe.autonode.translator

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.safe.autonode.exts.syntax.autoWrapToOption
import edu.jhu.seclab.safe.autonode.exts.cfg.{BlockLink, FuncCopy}
import edu.jhu.seclab.safe.autonode.query.safecfg.SafeCfg
import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGBlock, CFGFunction}

import scala.collection.mutable

class FuncTranslator extends AbsTranslator[CFGFunction] {

  private var newCfg: CFG = _ // NULLABLE
  private var holder: FunctionHolder = _ // NULLABLE

  def output(into: CFG): FuncTranslator = _selfReturn{ this.newCfg = into }
  def input(is: FunctionHolder):FuncTranslator = _selfReturn{ this.holder = is }

  private val createFromNone: PartialFunction[Option[CFGFunction], Option[CFGFunction]] = { case None =>
    println(s"CANNOT FIND FUNCTION WITH NAME ${holder.funcName}"); None
  }

  private val translateFromOld: PartialFunction[Option[CFGFunction], Option[CFGFunction]] = { case Some(oldFunc) =>
    val newFunc = newCfg.copyFunctionSignature(from=oldFunc)
    val blocksMap = new mutable.HashMap[AbsBlockHolder, CFGBlock]()
    val blockTranslator = new BlockTranslator().output(into=newFunc)
    holder.blocks.foreach { anBlock =>
      val safeBlock = blockTranslator.input(anBlock).translate()
      if (safeBlock nonEmpty) blocksMap.put(anBlock, safeBlock.get)
    }
    oldFunc.getCaptured.foreach(newFunc.addCaptured)
    blocksMap.foreach{case (holder, block)=>
      holder.parents.filter(blocksMap.contains).map(blocksMap).foreach(block.flowFrom)
    }
    newFunc
  }

  private val translateCore = translateFromOld orElse createFromNone

  override def translate():Option[CFGFunction] = translateCore(SafeCfg.query.function(whoseNameIs=holder.funcName))
}
