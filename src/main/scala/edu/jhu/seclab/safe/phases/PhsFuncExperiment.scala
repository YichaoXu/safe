package edu.jhu.seclab.safe.phases

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{Config, PhaseObj}
import kr.ac.kaist.safe.util.OptionKind
import com.github.sqlite4s.SQLiteConnection

import java.io.File
import scala.util.{Success, Try}

case object PhsFuncExperiment extends PhaseObj[Unit, ExperimentConfig, String] {
  val name: String = "experiment"
  val help: String = "run developing functions for experiment"
  override def apply(in: Unit, safeConfig: SafeConfig, config: ExperimentConfig): Try[String] = {

    val file = new File(s"${safeConfig.fileNames}.db")
    val db = new SQLiteConnection(file).open(allowCreate=false)

    Success(db.getTableColumnMetadata("main", "NodeTable", "id").toString)
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

