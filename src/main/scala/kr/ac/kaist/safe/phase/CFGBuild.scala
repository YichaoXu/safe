/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.phase

import edu.jhu.seclab.safe.autonode.CfgMixer
import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.util._

import java.io.File
import java.nio.file.Paths
import scala.util.{Properties, Success, Try}
import scala.sys.process._

// CFGBuild phase
case object CFGBuild extends PhaseObj[IRRoot, CFGBuildConfig, CFG] {
  val name: String = "cfgBuilder"
  val help: String = "Builds a control flow graph for JavaScript source files."

  private def autoNodeOptimize(safeCfg: CFG, safeConfig: SafeConfig, config: CFGBuildConfig): CFG = {
    if (!config.silent) println("⚠️  use AutoNode version of Control Flow Graph ⚠️")
    if(!config.autoNodeUseCache){
      val solverHome = Properties.envOrNone("SOLVER_HOME")
      if (solverHome isEmpty) throw new IllegalArgumentException("⚠️  Cannot find executable solver program ⚠️")
      val solverPath = Paths.get(Properties.envOrNone("SOLVER_HOME").get,  "generate_graph.py")
      val output = s"$solverPath ${safeConfig.fileNames.head} -Csmpq".!!
      if (! config.silent) println(output)
    }
    val anCfgNodes = new AutoNodeCfgHolder(
      nodesCsv = new File(s"${safeConfig.fileNames.head}.nodes.csv"),
      edgesCsv = new File(s"${safeConfig.fileNames.head}.rels.csv")
    )
    new CfgMixer(safeCfg, anCfgNodes).build()
  }

  def apply(ir: IRRoot, safeConfig: SafeConfig, config: CFGBuildConfig): Try[CFG] = {
    // Build CFG from IR.
    val cfgBuilder = new DefaultCFGBuilder(ir, safeConfig, config)
    val cfg = cfgBuilder.build()
    // Report errors.
    if (cfgBuilder.excLog.hasError) {
      println(cfg.relFileName + ":")
      println(cfgBuilder.excLog)
    }
    // Pretty print to file.
    config.outFile.foreach(out => {
      val (fw, writer) = Useful.fileNameToWriters(out)
      writer.write(cfg.toString(0))
      writer.close()
      fw.close()
      println("Dumped CFG to " + out)
    })
    if(! config.autoNode) Success(cfg)
    else Success(autoNodeOptimize(cfg, safeConfig, config))
  }

  def defaultConfig: CFGBuildConfig = CFGBuildConfig()
  val options: List[PhaseOption[CFGBuildConfig]] = List(
    ("silent", BoolOption[CFGBuildConfig](c => c.silent = true),
      "messages during CFG building are muted."),
    ("out", StrOption[CFGBuildConfig]((c, s) => c.outFile = Some(s)),
      "the resulting CFG will be written to the outfile."),
    ("auto-node", BoolOption[CFGBuildConfig](c => c.autoNode = true), "use auto node version cfg"),
    ("use-cache", BoolOption[CFGBuildConfig](c => c.autoNodeUseCache = true), "use cached file during auto node cfg generate")
  )
}

// CFGBuild phase config
case class CFGBuildConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None,
  var autoNode: Boolean = false,
  var autoNodeUseCache: Boolean = false
) extends Config
