package edu.jhu.seclab.safe.phases

import edu.jhu.seclab.safe.autonode.FromAutoNodeCfgBuilder
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{ Config, PhaseObj, PhaseOption }
import kr.ac.kaist.safe.util.{ BoolOption, StrOption }
import edu.jhu.seclab.safe.autonode.{ query => Querier }

import scala.sys.process._
import java.io.File
import java.nio.file.Paths

import scala.util.{ Properties, Try }

case object PhsAnCfgBuild extends PhaseObj[CFG, AnCfgBuildConfig, CFG] {

  val name: String = "anCfgBuild"
  val help: String = "generate cfg from csv files"

  override def apply(safeCfg: CFG, safeConfig: SafeConfig, config: AnCfgBuildConfig): Try[CFG] = Try {
    if (config.nodesSource.isEmpty || config.nodesSource.get == "origin") return Try(safeCfg)
    if (!config.silent) println("⚠️  Use AutoNode to generate CFG ⚠️")
    val jsName = safeConfig.fileNames.head
    val (nCsv, eCsv) = (new File(s"testnodes.csv"), new File(s"testrels.csv"))
    if (!nCsv.isFile || !eCsv.isFile) {
      if (!config.silent) println("⚠️  Cannot find previous csv file rebuilding... ⚠️")
      val SOLVER_HOME = Properties.envOrNone("SOLVER_HOME") match {
        case Some(str) => if (str.endsWith("/")) str.dropRight(0) else str
        case None => throw new IllegalArgumentException("⚠️  Cannot find executable solver program ⚠️")
      }
      val output = s"$SOLVER_HOME/generate_graph.py $jsName -Csmpq".!!
      if (!config.silent) println(output)
    }
    config.nodesSource match {
      case Some("csv") => Querier.sourceOfAutoNode(nCsv, eCsv)
      case Some("sql") =>
        val sqlFile = new File(s"$jsName.db")
        if (!sqlFile.isFile) {
          val SAFE_HOME = Properties.envOrNone("SAFE_HOME").get
          val output = s"$SAFE_HOME/bin/csv2sql --javascript=$jsName".!!
          if (!config.silent) println(output)
        }
        Querier.sourceOfAutoNode(sqlFile)
      case _ | None => throw new UnsupportedOperationException(s"Unsupported value for nodes: ${config.nodesSource}")
    }
    val autoCfg = new AutoNodeCfgHolder()
    if (!config.silent) println(autoCfg.toString)
    new FromAutoNodeCfgBuilder(safeCfg, autoCfg).build()
  }

  def defaultConfig: AnCfgBuildConfig = AnCfgBuildConfig()

  val options: List[PhaseOption[AnCfgBuildConfig]] = List(
    ("silent", BoolOption[AnCfgBuildConfig](c => c.silent = true),
      "messages during CFG building are muted."),
    ("out", StrOption[AnCfgBuildConfig]((c, s) => c.outFile = Some(s)),
      "the resulting CFG will be written to the outfile."),
    ("nodes", StrOption((c, s) => c.nodesSource = Some(s)),
      "origin: |default| use safe original cfg.\t" +
      "csv: use autonode cfg from csv\t" +
      "sql: like previous but using sql for optimisation"))
}
case class AnCfgBuildConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None,
  var nodesSource: Option[String] = None) extends Config

object AnCfgBuildConfig {
  def default: AnCfgBuildConfig = AnCfgBuildConfig()
}