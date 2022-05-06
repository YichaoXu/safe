package edu.jhu.seclab.safe.phases

import edu.jhu.seclab.safe.autonode.translator.CfgTranslator
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{ Config, PhaseObj, PhaseOption }
import kr.ac.kaist.safe.util.{ BoolOption, StrOption }

import java.io.File
import scala.util.{ Success, Try }

case object PhsFuncExperiment extends PhaseObj[CFG, ExperimentConfig, String] {
  val name: String = "funcExperiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: CFG, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {
    val anCfgNodes = new AutoNodeCfgHolder(
      nodesCsv = new File(config.node_csv_path),
      edgesCsv = new File(config.edge_csv_path))
    val translator = new CfgTranslator(in, anCfgNodes)
    println(anCfgNodes.toString)
    Success(translator.translate().toString(0))
  }
  def defaultConfig: ExperimentConfig = ExperimentConfig(node_csv_path = "", edge_csv_path = "")
  val options: List[PhaseOption[ExperimentConfig]] = List(
    ("silent", BoolOption(c => c.silent = true), "messages during CFG building are muted."),
    ("nodes", StrOption((c, s) => c.node_csv_path = s), ""),
    ("edges", StrOption((c, s) => c.edge_csv_path = s), ""))
}
case class ExperimentConfig(
  var silent: Boolean = false,
  var node_csv_path: String,
  var edge_csv_path: String) extends Config

