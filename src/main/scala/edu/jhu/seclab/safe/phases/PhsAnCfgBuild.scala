package edu.jhu.seclab.safe.phases

import edu.jhu.seclab.safe.autonode.FromAutoNodeCfgBuilder
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.{ Config, PhaseObj, PhaseOption }
import kr.ac.kaist.safe.util.BoolOption
import scala.sys.process._

import java.io.File
import java.nio.file.Paths
import scala.util.{ Properties, Success, Try }

case object PhsAnCfgBuild extends PhaseObj[CFG, AnCfgBuildConfig, CFG] {

  val name: String = "anCfgBuild"
  val help: String = "generate cfg from csv files"

  override def apply(in: CFG, safeConfig: SafeConfig, config: AnCfgBuildConfig): Try[CFG] = {
    if (!config.useCache) {
      val solverHome = Properties.envOrNone("SOLVER_HOME")
      if (solverHome isEmpty) throw new IllegalArgumentException("⚠️  Cannot find executable solver program ⚠️")
      val solverPath = Paths.get(Properties.envOrNone("SOLVER_HOME").get, "generate_graph.py")
      val output = s"$solverPath ${safeConfig.fileNames.head} -Csmpq".!!
      if (config.verbose) println(output)
    }
    val anCfgNodes = new AutoNodeCfgHolder(
      nodesCsv = new File(s"${safeConfig.fileNames.head}.nodes.csv"),
      edgesCsv = new File(s"${safeConfig.fileNames.head}.rels.csv"))
    Success(new FromAutoNodeCfgBuilder(in, anCfgNodes).build())
  }
  def defaultConfig: AnCfgBuildConfig = AnCfgBuildConfig.default
  val options: List[PhaseOption[AnCfgBuildConfig]] = List(
    ("verbose", BoolOption[AnCfgBuildConfig](c => c.verbose = true), "verbose during translating"),
    ("use-cache", BoolOption[AnCfgBuildConfig](c => c.useCache = true), "will use cached files during translating"))
}
case class AnCfgBuildConfig(
  var verbose: Boolean = false,
  var useCache: Boolean = false) extends Config

object AnCfgBuildConfig {
  def default: AnCfgBuildConfig = AnCfgBuildConfig()
}