package edu.jhu.mssi.seclab.extsafe.command

import edu.jhu.mssi.seclab.extsafe.phase.{ PhsCfgStatistic, StatisticOutput }
import kr.ac.kaist.safe.{ CmdCFGBuild, CommandObj }

case object CmdCfgStatistic extends CommandObj("cfgStatistic", CmdCFGBuild >> PhsCfgStatistic) {

  override def display(result: StatisticOutput): Unit = {
    println(s"The number of the edges is ${result.edge_count}\nThe number of nodes is ${result.node_count}")
  }
}
