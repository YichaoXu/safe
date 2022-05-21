package edu.jhu.seclab.safe.phases

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{ Config, PhaseObj }
import kr.ac.kaist.safe.util.OptionKind

import scala.util.{ Success, Try }

case object PhsFuncExperiment extends PhaseObj[CFG, ExperimentConfig, String] {
  val name: String = "experiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: CFG, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {
    Success("")
  }

  override def defaultConfig: ExperimentConfig = ExperimentConfig()
  override val options: List[(String, OptionKind[ExperimentConfig], String)] = Nil
}
case class ExperimentConfig(
  var verbose: Boolean = false,
  var useCache: Boolean = false) extends Config

object ExperimentConfig {
  def default: ExperimentConfig = ExperimentConfig()
}

