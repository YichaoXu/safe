package edu.jhu.seclab.safe

import edu.jhu.seclab.safe.commands.{ CmdAnCfgBuild, CmdCfgStatistic, CmdFuncExperiment }
import edu.jhu.seclab.safe.phases.{ PhsAnCfgBuild, PhsCfgStatistic, PhsFuncExperiment }
import kr.ac.kaist.safe.Command
import kr.ac.kaist.safe.phase.Phase

object SafeSupplement {
  val phases: List[Phase] = List(
    PhsCfgStatistic,
    PhsAnCfgBuild,
    PhsFuncExperiment)
  val commands: List[Command] = List(
    CmdCfgStatistic,
    CmdAnCfgBuild,
    CmdFuncExperiment)
}
