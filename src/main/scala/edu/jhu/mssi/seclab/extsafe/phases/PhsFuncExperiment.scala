package edu.jhu.mssi.seclab.extsafe.phases

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer.domain.register
import kr.ac.kaist.safe.nodes.cfg.{ CFG, Call }
import kr.ac.kaist.safe.phase.{ Config, PhaseObj, PhaseOption }
import kr.ac.kaist.safe.util.{ BoolOption, StrOption }

import scala.util.{ Success, Try }

case object PhsFuncExperiment extends PhaseObj[CFG, ExperimentConfig, String] {
  val name: String = "funcExperiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: CFG, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {
    print(config.nodecsv_path)
    print(config.edgecsv_path)
    Success("DONE")
    //    if (safeConfig.fileNames.length != 3)
    //      return Failure(new ExceptionInInitializerError("Need exactly two files for nodes and edges"))
    //    val srcJs = new File(safeConfig.fileNames.head)
    //    val nodesCsv = new File(safeConfig.fileNames(1))
    //    val edgesCsv = new File(safeConfig.fileNames(2))
    //    new AutoNodeCfgCsvTranslator(srcJs, nodesCsv, edgesCsv).build()

  }
  def defaultConfig: ExperimentConfig = ExperimentConfig()
  val options: List[PhaseOption[ExperimentConfig]] = List(
    ("silent", BoolOption(c => c.silent = true), "messages during CFG building are muted."),
    ("nodes", StrOption((c, s) => c.nodecsv_path = Some(s)), ""),
    ("edges", StrOption((c, s) => c.edgecsv_path = Some(s)), ""))
}
case class ExperimentConfig(
  var silent: Boolean = false,
  var nodecsv_path: Option[String] = None,
  var edgecsv_path: Option[String] = None) extends Config

