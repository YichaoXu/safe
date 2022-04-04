package edu.jhu.mssi.seclab.extsafe.cfg.builder

import edu.jhu.mssi.seclab.extsafe.csv.entity.Node
import kr.ac.kaist.safe.nodes.cfg.CFG

class AutoNodeConvertor(private val preCfg: CFG) {

  private val newCfg: CFG = new CFG(preCfg.ir, preCfg.globalVars)

  def addFunc(node: Node): FuncBuilder =
    new FuncBuilder(node.name, newCfg = newCfg, preCfg = preCfg)

  def build(): CFG = newCfg

}
