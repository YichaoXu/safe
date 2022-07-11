package edu.jhu.seclab.safe.commands

import edu.jhu.seclab.safe.phases.PhsAnCfgCompare
import kr.ac.kaist.safe.{CmdCFGBuild, CommandObj}

case object CmdAnCfgCompare extends CommandObj("anCfgCompare", CmdCFGBuild >> PhsAnCfgCompare) {

  override def display(report: String): Unit = println(report)

}

