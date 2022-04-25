package edu.jhu.seclab.extsafe.commands

import edu.jhu.seclab.extsafe.phases.PhsAutoCfgFilter
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.{ CmdTranslate, CommandObj }

case object CmdAutoCfgFilter extends CommandObj("autoCfgBuild", CmdTranslate >> PhsAutoCfgFilter) {

  override def display(cfg: CFG): Unit = println(cfg.toString(0))

}

