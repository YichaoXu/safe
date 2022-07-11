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

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.util._

import scala.util.{ Success, Try }

// CFGBuild phase
case object CFGBuild extends PhaseObj[IRRoot, CFGBuildConfig, CFG] {
  val name: String = "cfgBuilder"
  val help: String = "Builds a control flow graph for JavaScript source files."

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
    Success(cfg)
  }

  def defaultConfig: CFGBuildConfig = CFGBuildConfig()
  val options: List[PhaseOption[CFGBuildConfig]] = List(
    ("silent", BoolOption[CFGBuildConfig](c => c.silent = true),
      "messages during CFG building are muted."),
    ("out", StrOption[CFGBuildConfig]((c, s) => c.outFile = Some(s)),
      "the resulting CFG will be written to the outfile."))
}

// CFGBuild phase config
case class CFGBuildConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None,
  var nodesSource: Option[String] = None) extends Config
