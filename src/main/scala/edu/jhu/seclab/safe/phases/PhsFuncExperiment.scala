package edu.jhu.seclab.safe.phases

import edu.jhu.seclab.safe.autonode.CfgMixer
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{Config, PhaseObj, PhaseOption}
import kr.ac.kaist.safe.util.{BoolOption, StrOption}

import java.io.File
import java.nio.file.Paths
import scala.util.{Success, Try}
import scala.sys.process._
import scala.util.Properties

case object PhsFuncExperiment extends PhaseObj[CFG, ExperimentConfig, String] {
  val name: String = "experiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: CFG, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {
    val anCfgNodes = new AutoNodeCfgHolder(
      nodesCsv = new File(s"${safeConfig.fileNames.head}.nodes.csv"),
      edgesCsv = new File(s"${safeConfig.fileNames.head}.rels.csv")
    )
    Success(anCfgNodes.toString)
  }
  def defaultConfig: ExperimentConfig = ExperimentConfig.default
  val options: List[PhaseOption[ExperimentConfig]] = List(
    ("verbose", BoolOption[ExperimentConfig](c => c.verbose = true), "verbose during translating"),
    ("use-cache", BoolOption[ExperimentConfig](c => c.useCache = true), "will use cached files during translating")
  )
}
case class ExperimentConfig(
  var verbose: Boolean = false,
  var useCache: Boolean = false
) extends Config

object ExperimentConfig{
  def default: ExperimentConfig = ExperimentConfig()
}

