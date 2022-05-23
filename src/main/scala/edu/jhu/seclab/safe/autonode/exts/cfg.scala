package edu.jhu.seclab.safe.autonode.exts

import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import kr.ac.kaist.safe.nodes.ast.{ASTNode, ASTNodeInfo}
import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGBlock, CFGCallInst, CFGEdgeNormal, CFGFunction, CFGId, Call, LoopHead}
import kr.ac.kaist.safe.nodes.ir.IRNode

object cfg {

  implicit class FuncCopy(self: CFG) {
    def copyFunctionSignature(from: CFGFunction): CFGFunction = self.createFunction(
      name = from.name, ir = from.ir, argName = from.argumentsName,
      argVars = from.argVars, localVars = from.localVars, isUser = from.isUser)

    private def buildIrNode(holder: FunctionHolder): IRNode = {
      val astNode = new ASTNode {
        override val info: ASTNodeInfo = ASTNodeInfo(holder.funDef.namespace, None)
        override def toString(indent: Int): String = info.toString
      }
      new IRNode(astNode) { override def toString(indent: Int): String = astNode.toString() }
    }

    def emptyFunctionSignature(basedOn: FunctionHolder): CFGFunction = self.createFunction(
      name=basedOn.funcName, ir = buildIrNode(basedOn), argVars = Nil, localVars = Nil, argName = ""
    )
  }

  implicit class BlockLink(self: CFGBlock) {
    def flowTo(child: CFGBlock): Unit = {
      self.addSucc(CFGEdgeNormal, child)
      child.addPred(CFGEdgeNormal, self)
    }

    def flowFrom(parent: CFGBlock): Unit = {
      self.addPred(CFGEdgeNormal, parent)
      parent.addSucc(CFGEdgeNormal, self)
    }
  }

  implicit class CurryCallCreate(self: CFGFunction) {
    def createCallBlock(retVar: CFGId, outer: Option[LoopHead] = None)(callInstCons: Call => CFGCallInst): Call =
      self.createCall(callInstCons, retVar, outer)
  }

}
