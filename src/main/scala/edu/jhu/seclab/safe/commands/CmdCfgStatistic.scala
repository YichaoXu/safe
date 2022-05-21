package edu.jhu.seclab.safe.commands

import edu.jhu.seclab.safe.phases.{ PhsCfgStatistic, StatisticOutput }
import kr.ac.kaist.safe.{ CmdCFGBuild, CommandObj }

case object CmdCfgStatistic extends CommandObj("cfgStatistic", CmdCFGBuild >> PhsCfgStatistic) {

  override def display(result: StatisticOutput): Unit = {
    println(s"The number of the edges is ${result.edge_count}\nThe number of nodes is ${result.node_count}")
  }
}

