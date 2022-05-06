package edu.jhu.seclab.safe

import edu.jhu.seclab.safe.commands.{CmdAutoCfgFilter, CmdCfgStatistic, CmdFuncExperiment}
import edu.jhu.seclab.safe.phases.{PhsAutoCfgFilter, PhsCfgStatistic, PhsFuncExperiment}
import kr.ac.kaist.safe.Command
import kr.ac.kaist.safe.phase.Phase

object SafeSupplement {
  val phases: List[Phase] = List(
    PhsCfgStatistic,
    PhsAutoCfgFilter,
    PhsFuncExperiment)
  val commands: List[Command] = List(
    CmdCfgStatistic,
    CmdAutoCfgFilter,
    CmdFuncExperiment)
}
