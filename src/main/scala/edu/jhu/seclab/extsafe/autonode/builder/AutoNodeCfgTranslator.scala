package edu.jhu.seclab.extsafe.autonode.builder

import edu.jhu.seclab.extsafe.autonode.cfg.AutoNodeCfgHolder
import edu.jhu.seclab.extsafe.autonode.query.safecfg.SafeCfg
import edu.jhu.seclab.extsafe.autonode.cfg.block.AbsBlockHolder
import edu.jhu.seclab.extsafe.autonode.cfg.function.FunctionHolder
import edu.jhu.seclab.extsafe.autonode.query.csv.model.NodeType.{AST_CALL, AST_CATCH_LIST, AST_FUNC_DECL, AST_IF_ELEM, AST_SWITCH_CASE, AST_SWITCH_LIST, AST_TOP_LEVEL, AST_TRY}
import kr.ac.kaist.safe.nodes.cfg.{BranchLabel, CFG, CFGAllocArg, CFGAllocArray, CFGAssert, CFGBlock, CFGCall, CFGCallInst, CFGCatch, CFGConstruct, CFGDelete, CFGDeleteProp, CFGEnterCode, CFGExprStmt, CFGFunExpr, CFGFunction, CFGInst, CFGInternalCall, CFGNoOp, CFGNormalInst, CFGReturn, CFGStore, CFGStoreStringIdx, CFGThrow, CaseLabel, CatchLabel, NoLabel, NormalBlock, SwitchLabel, TryLabel, Call => CallBlock}
import kr.ac.kaist.safe.util.Span

class AutoNodeCfgTranslator(
  private val oldCfg: CFG,
  private val cfgNodes: AutoNodeCfgHolder,
) {

  SafeCfg.init(oldCfg)
  private val newCfg = new CFG(oldCfg.ir, oldCfg.globalVars)

  def translate(): CFG = {
    if(newCfg.getAllFuncs == Nil)
    cfgNodes.functions.foreach(newFunction)
    newCfg
  }

  private def convertCall(oldCall: CFGCallInst)(newBlock: CallBlock): CFGCallInst = oldCall match {
    case call:CFGCall => call.copy(block=newBlock)
    case construct: CFGConstruct => construct.copy(block=newBlock)
  }

  def moveInstructions(withIn: Span)(intoBlock: NormalBlock): Unit = {
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
    SafeCfg.query.instructions(withIn=withIn).foreach{
      inst => intoBlock.createInst(convert(inst))
    }
  }

  // TODO: OPTIMISATION FOR BINARY SEARCHING
  private def newBlock(bHolder: AbsBlockHolder, forFun: CFGFunction): Unit = bHolder.head match {
    case Some(node) if node is AST_IF_ELEM=>
      moveInstructions(withIn=bHolder.instrSpan)(intoBlock=forFun.createBlock(BranchLabel, None))
    case Some(_) if bHolder.last.get is AST_SWITCH_LIST=>
      moveInstructions(withIn=bHolder.instrSpan)(intoBlock=forFun.createBlock(SwitchLabel, None))
    case Some(node) if node is AST_SWITCH_CASE=>
      moveInstructions(withIn=bHolder.instrSpan)(intoBlock=forFun.createBlock(CaseLabel, None))
    case Some(_) if bHolder.last.get is AST_TRY=>
      moveInstructions(withIn=bHolder.instrSpan)(intoBlock=forFun.createBlock(TryLabel, None))
    case Some(node) if node is AST_CATCH_LIST=>
      moveInstructions(withIn=bHolder.instrSpan)(intoBlock=forFun.createBlock(CatchLabel, None))
    case Some(node) if node is AST_CALL=>
      SafeCfg.query.call(withIn=node.namespace) match {
        case Some(old) => forFun.createCall(convertCall(old.callInst), old.afterCall.retVar, None)
        case None => println(s"WARNING! CANNOT FIND ANY CALL BLOCK THERE FOR ${node.code}")
      }
    case Some(_) =>
      moveInstructions(withIn=bHolder.instrSpan)(intoBlock=forFun.createBlock(NoLabel, None))
    case None => println(f"WARNING! CANNOT FIND ANY THINGS THERE FOR:\n${bHolder.toString}")
  }

  private def newFunction(fHolder: FunctionHolder): Unit = {
    val funcName = fHolder.funDef.node_type match {
      case AST_TOP_LEVEL => "top-level"
      case AST_FUNC_DECL => fHolder.funDef.code
    }
    SafeCfg.query.function(whoseNameIs=funcName) match {
      case Some(old) =>
        val newFunc = newCfg.createFunction(
          name=old.name, ir=old.ir, argName=old.argumentsName,
          argVars=old.argVars, localVars=old.localVars, isUser=old.isUser
        )
        old.getCaptured.foreach(newFunc.addCaptured)
        fHolder.blocks.foreach(holder=> this.newBlock(holder, newFunc))
      case None => println(s"CANNOT FIND FUNCTION WITH NAME $funcName.")
    }
  }

}
