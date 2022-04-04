package edu.jhu.mssi.seclab

import edu.jhu.mssi.seclab.extsafe.commands.{ CmdAutoCfgFilter, CmdCfgStatistic, CmdFuncExperiment }
import edu.jhu.mssi.seclab.extsafe.phases.{ PhsAutoCfgFilter, PhsCfgStatistic, PhsFuncExperiment }
import kr.ac.kaist.safe.{ Command, CommandObj }
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
