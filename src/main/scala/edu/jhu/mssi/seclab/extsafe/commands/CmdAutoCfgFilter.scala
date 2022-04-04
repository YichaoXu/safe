package edu.jhu.mssi.seclab.extsafe.commands

import kr.ac.kaist.safe.nodes.cfg.CFG
import edu.jhu.mssi.seclab.extsafe.phases.PhsAutoCfgFilter
import kr.ac.kaist.safe.{ CmdTranslate, CommandObj }

case object CmdAutoCfgFilter extends CommandObj("autoCfgBuild", CmdTranslate >> PhsAutoCfgFilter) {

  override def display(cfg: CFG): Unit = println(cfg.toString(0))

}

