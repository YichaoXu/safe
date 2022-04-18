package edu.jhu.mssi.seclab.extsafe.phases

import edu.jhu.mssi.seclab.extsafe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{Config, PhaseObj, PhaseOption}
import kr.ac.kaist.safe.util.{BoolOption, StrOption}

import java.io.File
import scala.util.{Success, Try}

case object PhsFuncExperiment extends PhaseObj[CFG, ExperimentConfig, String] {
  val name: String = "funcExperiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: CFG, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {
    val edgeCsvFile = new File(config.edge_csv_path)
    val nodeCsvFile = new File(config.node_csv_path)
    val anCfgNodes = new AutoNodeCfgHolder(nodeCsvFile, edgeCsvFile)
    print(anCfgNodes)
    Success("")
    //    if (safeConfig.fileNames.length != 3)
    //      return Failure(new ExceptionInInitializerError("Need exactly two files for nodes and edges"))
    //    val srcJs = new File(safeConfig.fileNames.head)
    //    val nodesCsv = new File(safeConfig.fileNames(1))
    //    val edgesCsv = new File(safeConfig.fileNames(2))
    //    new AutoNodeCfgCsvTranslator(srcJs, nodesCsv, edgesCsv).build()

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
  var edge_csv_path: String
) extends Config

