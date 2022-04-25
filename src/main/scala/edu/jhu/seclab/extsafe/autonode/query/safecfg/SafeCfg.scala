package edu.jhu.seclab.extsafe.autonode.query.safecfg

import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock, CFGFunction, CFGInst, Call => CallBlock }
import kr.ac.kaist.safe.util.Span

class SafeCfg(val core: CFG) {

  object SpanUtil {
    def isContained(outer: Span, inner: Span): Boolean = outer.begin <= inner.begin && outer.end >= inner.end
  }

  lazy val functions: List[CFGFunction] = core.getAllFuncs
  def function(whoseNameIs: String): Option[CFGFunction] = core.getAllFuncs.find(func => func.name == whoseNameIs)

  lazy val blocks: List[CFGBlock] = core.getAllBlocks

  def call(withIn: Span): Option[CallBlock] = blocks
    .filter(_.isInstanceOf[CallBlock]).map(_.asInstanceOf[CallBlock])
    .find { call => SpanUtil.isContained(outer = withIn, inner = call.span) }

  lazy val instructions: List[CFGInst] = this.core.getAllFuncs.flatten { _.getAllBlocks }.flatten { _.getInsts }
  def instructions(withIn: Span): List[CFGInst] = instructions
    .filter { instr => SpanUtil.isContained(outer = withIn, inner = instr.span) }

  def instructions(ofFuncName: String): List[CFGInst] = function(whoseNameIs=ofFuncName) match {
    case Some(func) => func.getAllBlocks.flatten { _.getInsts }
  }

}

object SafeCfg {
  private var core: Option[SafeCfg] = None
  def init(cfg: CFG): Unit = {
    core = Some(new SafeCfg(cfg))
  }
  lazy val query: SafeCfg = core.get
}
