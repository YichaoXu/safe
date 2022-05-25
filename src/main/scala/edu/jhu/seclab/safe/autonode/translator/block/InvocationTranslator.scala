package edu.jhu.seclab.safe.autonode.translator.block

import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import edu.jhu.seclab.safe.autonode.exts.cfg.CurryCallCreate
import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import edu.jhu.seclab.safe.autonode.{ query => Querier }
import kr.ac.kaist.safe.nodes.cfg.{ CFGCall, CFGConstruct, CFGFunction, Call => CallBlock }

class InvocationTranslator extends AbsBlockTranslator[CallBlock] {

  private var source: CallBlockHolder = _ // NULLABLE
  private var destination: CFGFunction = _ // NULLABLE

  override def input(is: AbsBlockHolder): InvocationTranslator = _selfReturn { this.source = is.asInstanceOf[CallBlockHolder] }
  override def output(into: CFGFunction): InvocationTranslator = _selfReturn { this.destination = into }

  override def translate(): Option[CallBlock] = {
    val oldCall = Querier.safeCfg.calls(ofFuncName = destination.name).find(_.span.isCrossover(source.head.namespace))
    if (oldCall isEmpty) return None
    destination.createCallBlock(oldCall.get.afterCall.retVar)(newCall => oldCall.get.callInst match {
      case call: CFGCall => call.copy(block = oldCall.get)
      case construct: CFGConstruct => construct.copy(block = oldCall.get)
    })
  }
}
