package edu.jhu.seclab.safe.phases

import edu.jhu.seclab.safe.autonode.{AnCfgComparer, FromAutoNodeCfgBuilder, query => Querier}
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{Config, PhaseObj, PhaseOption}
import kr.ac.kaist.safe.util.{BoolOption, StrOption}

import scala.sys.process._
import java.io.File
import scala.util.{Properties, Try}

case object PhsAnCfgCompare extends PhaseObj[CFG, AnCfgCompareConfig, String] {

  val name: String = "anCfgBuild"
  val help: String = "generate cfg from csv files"

  override def apply(safeCfg: CFG, safeConfig: SafeConfig, config: AnCfgCompareConfig): Try[String] = Try {
    if (!config.silent) println("⚠️  Use AutoNode to generate CFG ⚠️")
    val jsFile = new File(safeConfig.fileNames.head)
    val (nCsv, eCsv) = (new File(s"./logs/nodes.csv"), new File(s"./logs/rels.csv"))
    if (!nCsv.isFile || !eCsv.isFile) {
      if (!config.silent) println("⚠️  Cannot find previous csv file rebuilding... ⚠️")
      val SOLVER_HOME = Properties.envOrNone("SOLVER_HOME") match {
        case Some(str) => if (str.endsWith("/")) str.dropRight(0) else str
        case None => throw new IllegalArgumentException("⚠️  Cannot find executable solver program ⚠️")
      }
      val output = s"$SOLVER_HOME/generate_graph.py ${jsFile.getAbsolutePath} -Cspm".!!
      if (!config.silent) println(output)
    }

    val sqlFile = new File(s"./logs/${jsFile.getName}.db")
    if (!sqlFile.isFile) {
      val SAFE_HOME = Properties.envOrNone("SAFE_HOME").get
      val output = s"$SAFE_HOME/bin/csv2sql --javascript=${jsFile.getPath}".!!
      if (!config.silent) println(output)
    }
    Querier.sourceOfAutoNode(sqlFile)
    val autoCfg = new AutoNodeCfgHolder()
    if (!config.silent) println(autoCfg.toString)
    val comparer = new AnCfgComparer(safeCfg, autoCfg)
    config.level match {
      case Some("functions") => comparer.funcsCompare()
      case Some("blocks") => comparer.blocksCompare()
      case Some("instructions") => comparer.instrsCompare()
    }
  }

  def defaultConfig: AnCfgCompareConfig = AnCfgCompareConfig()

  val options: List[PhaseOption[AnCfgCompareConfig]] = List(
    ("silent", BoolOption[AnCfgCompareConfig](c => c.silent = true),
      "messages during CFG building are muted."),
    ("level", StrOption[AnCfgCompareConfig]((c, s) => c.level = Some(s)),
      "the comparing levels (can be functions, block or instruction)")
  )
}
case class AnCfgCompareConfig(
  var silent: Boolean = false,
  var level: Option[String] = Some("functions")
) extends Config

object AnCfgCompareConfig {
  def default: AnCfgCompareConfig = AnCfgCompareConfig()
}