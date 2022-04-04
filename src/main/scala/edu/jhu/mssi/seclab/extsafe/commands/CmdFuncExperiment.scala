package edu.jhu.mssi.seclab.extsafe.commands

import edu.jhu.mssi.seclab.extsafe.phases.PhsFuncExperiment
import kr.ac.kaist.safe.{ CmdBase, CmdCFGBuild, CommandObj }

case object CmdFuncExperiment extends CommandObj("funcExperiment", CmdCFGBuild >> PhsFuncExperiment) {

  override def display(result: String): Unit = println(result)

}
