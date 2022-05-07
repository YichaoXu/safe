package edu.jhu.seclab.safe.commands

import edu.jhu.seclab.safe.phases.PhsAnCfgBuild
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.{CmdCFGBuild, CmdTranslate, CommandObj}

case object CmdAnCfgBuild extends CommandObj("anCfgBuild", CmdCFGBuild >> PhsAnCfgBuild) {

  override def display(cfg: CFG): Unit = println(cfg.toString(0))

}

