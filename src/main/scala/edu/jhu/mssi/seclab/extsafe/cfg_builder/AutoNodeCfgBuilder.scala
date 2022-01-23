package edu.jhu.mssi.seclab.extsafe.cfg_builder

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.phase.CFGBuildConfig
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.cfg.CFG
import edu.jhu.mssi.seclab.extsafe.nodes.AutoNodeCfg

class AutoNodeCfgBuilder(
  ir: IRRoot,
  safeConfig: SafeConfig,
  config: CFGBuildConfig
) extends DefaultCFGBuilder(ir, safeConfig, config) {

  override val cfg: CFG = new AutoNodeCfg(super.cfg)

}