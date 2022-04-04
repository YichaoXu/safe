package edu.jhu.mssi.seclab.extsafe.cfg

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.phase.CFGBuildConfig
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.cfg.CFG
import edu.jhu.mssi.seclab.extsafe.nodes.AutoNodeCfg
import kr.ac.kaist.safe.errors.ExcLog

class AutoNodeCfgFilterBuilder(
  ir: IRRoot,
  safeConfig: SafeConfig,
  config: CFGBuildConfig) extends DefaultCFGBuilder(ir, safeConfig, config) {

  override final protected def init: (CFG, ExcLog) = {
    val (tmpCfg, tmpExcLog) = super.init
    (new AutoNodeCfg(tmpCfg, "", ""), tmpExcLog)
  }

}