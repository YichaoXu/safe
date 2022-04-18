package edu.jhu.mssi.seclab.extsafe.autonode.csv.query

import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGFunction}

class SafeCfg(private val cfgCore: CFG) extends AbsQuerier {

  def core:CFG = this.cfgCore
  def byName(name: String): Option[CFGFunction] = {
    this.core.getAllFuncs.find(func=> func.name == name)
  }
}

object SafeCfg {
  private var core: Option[SafeCfg] = None
  def init(cfg: CFG): Unit = {
    core = Some(new SafeCfg(cfg))
  }
  lazy val query: SafeCfg = core.get
}
