package edu.jhu.seclab.safe.autonode.translator.block

import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import edu.jhu.seclab.safe.autonode.exts.cfg.CurryCallCreate
import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import edu.jhu.seclab.safe.autonode.{ query => Querier }
import kr.ac.kaist.safe.nodes.cfg.{ CFGCall, CFGConstruct, CFGFunction, Call => CallBlock }

class CallBlockTranslator extends AbsBlockTranslator[CallBlock] {

  private var dataFrom: CallBlockHolder = _ // NULLABLE
  private var storeInto: CFGFunction = _ // NULLABLE

  override def input(is: AbsBlockHolder): CallBlockTranslator = _selfReturn { this.dataFrom = is.asInstanceOf[CallBlockHolder] }
  override def output(into: CFGFunction): CallBlockTranslator = _selfReturn { this.storeInto = into }

  override def translate(): Option[CallBlock] = {
    val oldCall = Querier.safeCfg.calls(inside = storeInto.name).find(_.span.isCrossover(dataFrom.head.namespace))
    if (oldCall isEmpty) return None
    storeInto.createCallBlock(oldCall.get.afterCall.retVar)(newCall => oldCall.get.callInst match {
      case call: CFGCall => call.copy(block = oldCall.get)
      case construct: CFGConstruct => construct.copy(block = oldCall.get)
    })
  }
}
