package edu.jhu.seclab.safe.commands

import edu.jhu.seclab.safe.phases.PhsAutoCfgFilter
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.{ CmdTranslate, CommandObj }

case object CmdAutoCfgFilter extends CommandObj("autoCfgBuild", CmdTranslate >> PhsAutoCfgFilter) {

  override def display(cfg: CFG): Unit = println(cfg.toString(0))

}

