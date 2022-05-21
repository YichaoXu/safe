package edu.jhu.seclab.safe.autonode.exts

import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock, CFGCallInst, CFGEdgeNormal, CFGFunction, CFGId, Call, LoopHead }

object cfg {

  implicit class FuncCopy(self: CFG) {
    def copyFunctionSignature(from: CFGFunction): CFGFunction = self.createFunction(
      name = from.name, ir = from.ir, argName = from.argumentsName,
      argVars = from.argVars, localVars = from.localVars, isUser = from.isUser)
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
