package edu.jhu.seclab.safe.phases

import kr.ac.kaist.safe.SafeConfig
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
    ???
  }

  def defaultConfig: CFGBuildConfig = CFGBuildConfig()
  val options: List[PhaseOption[CFGBuildConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during CFG building are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting CFG will be written into the outfile."))
}