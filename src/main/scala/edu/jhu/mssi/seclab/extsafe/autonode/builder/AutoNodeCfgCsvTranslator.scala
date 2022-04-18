package edu.jhu.mssi.seclab.extsafe.autonode.builder

import edu.jhu.mssi.seclab.extsafe.autonode.cfg.AutoNodeCfgHolder
import kr.ac.kaist.safe.nodes.cfg.CFG

class AutoNodeCfgCsvTranslator(
  private val oldCfg: CFG,
  private val cfgNodes: AutoNodeCfgHolder,
) extends AbsBuilder {

  private val newCfg = new CFG(oldCfg.ir, oldCfg.globalVars)

  def build(): CFG = {
    newCfg
  }

}
