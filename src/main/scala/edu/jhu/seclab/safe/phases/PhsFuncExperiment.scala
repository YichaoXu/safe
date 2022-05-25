package edu.jhu.seclab.safe.phases

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.phase.{ Config, PhaseObj }
import kr.ac.kaist.safe.util.OptionKind
import edu.jhu.seclab.safe.autonode.{ query => Querier }

import java.io.File
import scala.util.{ Success, Try }

case object PhsFuncExperiment extends PhaseObj[Unit, ExperimentConfig, String] {
  val name: String = "experiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: Unit, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {
    Querier.sourceOfAutoNode(new File(s"${safeConfig.fileNames.head}.db"))
    val two = Querier.autoNode.fileEntry
    println(s"NODE2: ${two.toString}")
    Success(Querier.autoNode.flowFrom(two.get.core).toString)
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

