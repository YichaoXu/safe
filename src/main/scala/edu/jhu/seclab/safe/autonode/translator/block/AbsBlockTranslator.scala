package edu.jhu.seclab.safe.autonode.translator.block

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.translator.AbsTranslator
import kr.ac.kaist.safe.nodes.cfg.{ CFGBlock, CFGFunction }

abstract class AbsBlockTranslator[Target <: CFGBlock] extends AbsTranslator[Target] {
  override type InputType = AbsBlockHolder
  override type DestinationType = CFGFunction
}
