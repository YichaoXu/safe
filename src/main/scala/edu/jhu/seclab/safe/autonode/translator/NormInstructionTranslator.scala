package edu.jhu.seclab.safe.autonode.translator

import edu.jhu.seclab.safe.autonode.exts.syntax.autoWrapToOption
import kr.ac.kaist.safe.nodes.cfg._

class NormInstructionTranslator extends AbsTranslator[CFGNormalInst]{

  private var block: NormalBlock = _
  private var instruction: CFGInst = _

  def output(to: NormalBlock): NormInstructionTranslator = _selfReturn{ this.block = to }
  def input(is: CFGInst): NormInstructionTranslator = _selfReturn{ this.instruction = is }

  override def translate(): Option[CFGNormalInst] =
    block.createInst( newBlock => instruction match {
      case inst: CFGAllocArray => inst.copy(block=newBlock)
      case inst: CFGAllocArg => inst.copy(block=newBlock)
      case inst: CFGEnterCode => inst.copy(block=newBlock)
      case inst: CFGExprStmt => inst.copy(block=newBlock)
      case inst: CFGDelete => inst.copy(block=newBlock)
      case inst: CFGDeleteProp => inst.copy(block=newBlock)
      case inst: CFGStore => inst.copy(block=newBlock)
      case inst: CFGStoreStringIdx => inst.copy(block=newBlock)
      case inst: CFGFunExpr => inst.copy(block=newBlock)
      case inst: CFGAssert => inst.copy(block=newBlock)
      case inst: CFGCatch => inst.copy(block=newBlock)
      case inst: CFGReturn => inst.copy(block=newBlock)
      case inst: CFGThrow => inst.copy(block=newBlock)
      case inst: CFGNoOp => inst.copy(block=newBlock)
      case inst: CFGInternalCall => inst.copy(block=newBlock)
    })
}
