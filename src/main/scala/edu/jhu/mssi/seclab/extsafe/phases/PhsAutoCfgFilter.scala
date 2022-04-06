package edu.jhu.mssi.seclab.extsafe.phases

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.phase.{ CFGBuildConfig, Config, PhaseObj, PhaseOption }
import kr.ac.kaist.safe.util.{ BoolOption, StrOption, Useful }

import scala.util.{ Success, Try }

case object PhsAutoCfgFilter extends PhaseObj[IRRoot, CFGBuildConfig, CFG] {
  val name: String = "autoCfgBuilder"
  val help: String =
    "Builds a control flow graph for JavaScript source files."
  def apply(ir: IRRoot, safeConfig: SafeConfig, config: CFGBuildConfig): Try[CFG] = {
    // Build CFG from IR.
    val cfgBuilder = new AutoNodeCfgFilterBuilder(ir, safeConfig, config)
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
    ("silent", BoolOption(c => c.silent = true),
      "messages during CFG building are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting CFG will be written to the outfile."))
}