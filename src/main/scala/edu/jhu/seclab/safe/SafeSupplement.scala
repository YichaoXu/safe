package edu.jhu.seclab.safe

import edu.jhu.seclab.safe.commands.{ CmdAnCfgBuild, CmdCfgStatistic, CmdFuncExperiment, CmdAnCfgCompare }
import edu.jhu.seclab.safe.phases.{ PhsAnCfgBuild, PhsCfgStatistic, PhsFuncExperiment, PhsAnCfgCompare }
import kr.ac.kaist.safe.Command
import kr.ac.kaist.safe.phase.Phase

object SafeSupplement {
  val phases: List[Phase] = List(
    PhsCfgStatistic,
    PhsAnCfgBuild,
    PhsAnCfgCompare,
    PhsFuncExperiment)
  val commands: List[Command] = List(
    CmdCfgStatistic,
    CmdAnCfgBuild,
    CmdAnCfgCompare,
    CmdFuncExperiment)
}
