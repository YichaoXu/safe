package edu.jhu.seclab.safe.autonode.translator

import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.query.safecfg.SafeCfg
import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.safe.autonode.query.csv.model.NodeType.{AST_CALL, AST_CATCH_LIST, AST_IF_ELEM, AST_SWITCH_CASE, AST_SWITCH_LIST, AST_TOP_LEVEL, AST_TRY}
import kr.ac.kaist.safe.nodes.cfg.{BranchLabel, CFG, CFGAllocArg, CFGAllocArray, CFGAssert, CFGBlock, CFGCall, CFGCallInst, CFGCatch, CFGConstruct, CFGDelete, CFGDeleteProp, CFGEnterCode, CFGExprStmt, CFGFunExpr, CFGFunction, CFGInst, CFGInternalCall, CFGNoOp, CFGReturn, CFGStore, CFGStoreStringIdx, CFGThrow, CaseLabel, CatchLabel, NoLabel, NormalBlock, SwitchLabel, TryLabel, Call => CallBlock}

class CfgTranslator(
  private val oldCfg: CFG,
  private val cfgNodes: AutoNodeCfgHolder
) {
  SafeCfg.init(oldCfg)
  private val newCfg = new CFG(oldCfg.ir, oldCfg.globalVars)

  def translate(): CFG = {
    newCfg.getAllFuncs match {
      case Nil => cfgNodes.functions.foreach(newFunction)
      case _ => () //DO NOTHING
    }
    newCfg
  }

  private def convertCall(oldCall: CFGCallInst)(newBlock: CallBlock): CFGCallInst = oldCall match {
    case call:CFGCall => call.copy(block=newBlock)
    case construct: CFGConstruct => construct.copy(block=newBlock)
  }

  def translateInstructions(from: AbsBlockHolder)(into: NormalBlock): NormalBlock = {
    val convert = (oldInstr: CFGInst) => (newBlock: NormalBlock) => oldInstr match {
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
    }
    SafeCfg.query.instructions(ofFuncName=into.func.name)
      .filter{inst => from.shouldContain(inst) && !inst.isInstanceOf[CFGCallInst]}.reverse
      .foreach{inst => into.createInst(convert(inst))}
    into
  }

  // TODO: OPTIMISATION FOR BINARY SEARCHING
  private def newBlock(bHolder: AbsBlockHolder, forFun: CFGFunction): Option[CFGBlock] = bHolder.head match {
    case Some(node) if node is AST_IF_ELEM=>
      translateInstructions(from=bHolder)(into=forFun.createBlock(BranchLabel, None))
    case Some(_) if bHolder.last.get is AST_SWITCH_LIST=>
      translateInstructions(from=bHolder)(into=forFun.createBlock(SwitchLabel, None))
    case Some(node) if node is AST_SWITCH_CASE=>
      translateInstructions(from=bHolder)(into=forFun.createBlock(CaseLabel, None))
    case Some(_) if bHolder.last.get is AST_TRY=>
      translateInstructions(from=bHolder)(into=forFun.createBlock(TryLabel, None))
    case Some(node) if node is AST_CATCH_LIST=>
      translateInstructions(from=bHolder)(into=forFun.createBlock(CatchLabel, None))
    case Some(node) if (node is AST_CALL) || bHolder.isInstanceOf[CallBlockHolder]=>
      SafeCfg.query.calls(ofFuncName=forFun.name).find{_.span.isCrossover(to=node.namespace)} match {
        case Some(old) => forFun.createCall(convertCall(old.callInst), old.afterCall.retVar, None)
        case None => None
      }
    case Some(node) if node.isFuncEntry => forFun.entry
    case Some(node) if node.isFuncEnd => forFun.exit
    case Some(node) if node.isBlockEnd => None
    case Some(_) => translateInstructions(from=bHolder)(into=forFun.createBlock(NoLabel, None))
    case None => None
  }

  private def newFunction(holder: FunctionHolder): Unit = {
    new FuncTranslator().input(holder).output(into=newCfg).translate()
  }

}
