package edu.jhu.seclab.safe.autonode

import edu.jhu.seclab.safe.autonode.cfg.AutoNodeCfgHolder
import edu.jhu.seclab.safe.autonode.query.safecfg.SafeCfg
import edu.jhu.seclab.safe.autonode.translator.FuncTranslator
import kr.ac.kaist.safe.nodes.cfg._

class CfgBuilder(
  private val oldCfg: CFG,
  private val cfgNodes: AutoNodeCfgHolder
) {

  SafeCfg.init(oldCfg)
  private var target: Option[CFG] = None

  def build(): CFG = {
    if (target nonEmpty) return target.get
    val newCfg = new CFG(oldCfg.ir, oldCfg.globalVars)
    val funcTranslator = new FuncTranslator().output(into=newCfg)
    cfgNodes.functions.foreach { newFuncHolder => funcTranslator.input(newFuncHolder).translate()}
    target = Some(newCfg)
    newCfg
  }
}
