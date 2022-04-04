package edu.jhu.mssi.seclab.extsafe.csv.builder

import kr.ac.kaist.safe.nodes.cfg.CFGId
import kr.ac.kaist.safe.nodes.ir.IRNode

case class FuncParametersHolder(
  argumentsName: String,
  argVars: List[CFGId],
  localVars: List[CFGId],
  name: String,
  ir: IRNode,
  isUser: Boolean) {
}
