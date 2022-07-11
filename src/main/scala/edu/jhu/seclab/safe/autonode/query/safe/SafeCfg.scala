package edu.jhu.seclab.safe.autonode.query.safe

import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import edu.jhu.seclab.safe.autonode.cfg.function.FunctionHolder
import kr.ac.kaist.safe.util.Span
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock, CFGFunction, CFGInst, Call => CallBlock }

class SafeCfg(val core: CFG) {

  lazy val functions: List[CFGFunction] = core.getAllFuncs
  def exists(funcName: String): Boolean = core.getAllFuncs.exists(func => func.name == funcName)
  def function(ofHolder: FunctionHolder): Option[CFGFunction] =
    this.function(whoseNameIs = ofHolder.funcName) orElse this.function(whoseRangeIs = ofHolder.namespace)
  def function(whoseNameIs: String): Option[CFGFunction] =
    core.getAllFuncs.find(func => func.name == whoseNameIs)
  def function(whoseRangeIs: Span): Option[CFGFunction] =
    core.getAllFuncs.find(func => func.span.isSameLiterately(to = whoseRangeIs))

  lazy val blocks: List[CFGBlock] = core.getAllBlocks

  def calls(inside: String): List[CallBlock] = function(whoseNameIs = inside) match {
    case Some(func) => func.getAllBlocks.filter(_.isInstanceOf[CallBlock]).map(_.asInstanceOf[CallBlock])
    case None => Nil
  }

  lazy val instructions: List[CFGInst] = this.core.getAllFuncs.flatten { _.getAllBlocks }.flatten { _.getInsts }

  private var spanInstCache: Map[String, List[CFGInst]] = Map()
  def instructions(withIn: Span): List[CFGInst] = namedInstCache.getOrElse(withIn.toString, {
    val instructions = this.instructions.filter { _.span.isContained(into = withIn) }
    this.spanInstCache = Map(withIn.toString -> instructions)
    instructions
  })

  private var namedInstCache: Map[String, List[CFGInst]] = Map()
  def instructions(ofFuncName: String): List[CFGInst] = namedInstCache.getOrElse(ofFuncName, {
    val instructions = this.function(whoseNameIs = ofFuncName) match {
      case Some(func) => func.getAllBlocks.flatten { _.getInsts }
      case None => Nil
    }
    this.namedInstCache = Map(ofFuncName -> instructions)
    instructions
  })

}