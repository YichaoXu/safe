package edu.jhu.mssi.seclab.extsafe.cfg.builder

import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGFunction}

class FuncFactory(
  private val name: String,
  private val newCfg: CFG,
  private val preCfg: CFG
) extends AbstractBuilder[CFGFunction] {


  override def build(): Option[CFGFunction] = ???
}