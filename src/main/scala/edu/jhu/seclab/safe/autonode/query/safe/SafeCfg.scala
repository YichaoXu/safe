package edu.jhu.seclab.safe.autonode.query.safe

import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock, CFGFunction, CFGInst, Call => CallBlock }
import kr.ac.kaist.safe.util.Span

class SafeCfg(val core: CFG) {

  lazy val functions: List[CFGFunction] = core.getAllFuncs
  def function(whoseNameIs: String): Option[CFGFunction] = core.getAllFuncs.find(func => func.name == whoseNameIs)

  lazy val blocks: List[CFGBlock] = core.getAllBlocks

  def calls(ofFuncName: String): List[CallBlock] = function(whoseNameIs = ofFuncName) match {
    case Some(func) => func.getAllBlocks.filter(_.isInstanceOf[CallBlock]).map(_.asInstanceOf[CallBlock])
    case None => Nil
  }

  lazy val instructions: List[CFGInst] = this.core.getAllFuncs.flatten { _.getAllBlocks }.flatten { _.getInsts }
  def instructions(withIn: Span): List[CFGInst] = instructions.filter { _.span.isContained(into = withIn) }

  def instructions(ofFuncName: String): List[CFGInst] = function(whoseNameIs = ofFuncName) match {
    case Some(func) => func.getAllBlocks.flatten { _.getInsts }
    case None => Nil
  }

}

object SafeCfg {
  private var core: Option[SafeCfg] = None
  def init(cfg: CFG): Unit = {
    core = Some(new SafeCfg(cfg))
  }
  lazy val query: SafeCfg = core.get
}
