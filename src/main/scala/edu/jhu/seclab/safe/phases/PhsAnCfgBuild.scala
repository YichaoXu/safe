package edu.jhu.seclab.safe.phases

import edu.jhu.seclab.safe.autonode.FromAutoNodeCfgBuilder
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{Config, PhaseObj, PhaseOption}
import kr.ac.kaist.safe.util.{BoolOption, StrOption}
import edu.jhu.seclab.safe.autonode.{query => Querier}

import scala.sys.process._
import java.io.File
import java.nio.file.Paths
import scala.util.{Properties, Try}

case object PhsAnCfgBuild extends PhaseObj[CFG, AnCfgBuildConfig, CFG] {

  val name: String = "anCfgBuild"
  val help: String = "generate cfg from csv files"

  private def autoNodeOptimize(safeCfg: CFG, safeConfig: SafeConfig, config: AnCfgBuildConfig): CFG = {
    if (!config.silent) println("⚠️  use AutoNode version of Control Flow Graph ⚠️")
    val jsFileName = safeConfig.fileNames.head
    val (nCsv, eCsv) = (new File(s"$jsFileName.nodes.csv"), new File(s"$jsFileName.rels.csv"))
    if (!nCsv.isFile || !eCsv.isFile) {
      val solverHome = Properties.envOrNone("SOLVER_HOME")
      if (solverHome isEmpty) throw new IllegalArgumentException("⚠️  Cannot find executable solver program ⚠️")
      val solverPath = Paths.get(Properties.envOrNone("SOLVER_HOME").get, "generate_graph.py")
      val output = s"$solverPath ${jsFileName} -Csmpq".!!
      if (!config.silent) println(output)
    }
    config.nodesSource match {
      case Some("csv") => Querier.sourceOfAutoNode(nCsv, eCsv)
      case Some("sql") =>
        val sqlFile = new File(s"$jsFileName.db")
        if(!sqlFile.isFile) {
          val output = s"${Properties.envOrNone("SAFE_HOME")} $jsFileName.db -Csmpq".!!
          if (!config.silent) println(output)
        }
        Querier.sourceOfAutoNode(sqlFile)
      case None | Some("origin") => throw new UnsupportedOperationException("IMPOSSIBLE")
      case _ => throw new UnsupportedOperationException("Unsupported value for {nodes}")
    }
    val autoCfg = new AutoNodeCfgHolder()
    if(!config.silent) println(autoCfg.functions.map(_.funcName).toString)
    new FromAutoNodeCfgBuilder(safeCfg, autoCfg).build()
  }

  override def apply(safeCfg: CFG, safeConfig: SafeConfig, config: AnCfgBuildConfig): Try[CFG] = Try{
    if(config.nodesSource.isEmpty || config.nodesSource.get == "origin") safeCfg
    else autoNodeOptimize(safeCfg, safeConfig, config)
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
        "sql: like previous but using sql for optimisation"
    )
  )
}
case class AnCfgBuildConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None,
  var nodesSource: Option[String] = None
) extends Config

object AnCfgBuildConfig {
  def default: AnCfgBuildConfig = AnCfgBuildConfig()
}