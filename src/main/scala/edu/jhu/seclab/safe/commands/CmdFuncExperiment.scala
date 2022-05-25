package edu.jhu.seclab.safe.commands

import edu.jhu.seclab.safe.phases.PhsFuncExperiment
import kr.ac.kaist.safe.{ CmdBase, CommandObj }

case object CmdFuncExperiment extends CommandObj("experiment", CmdBase >> PhsFuncExperiment) {

  override def display(result: String): Unit = println(result)

}
