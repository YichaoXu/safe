package edu.jhu.mssi.seclab.extsafe.cfg.builder

import edu.jhu.mssi.seclab.extsafe.cfg.utils.CfgVarsScope
import edu.jhu.mssi.seclab.extsafe.utils.CfgUtils
import kr.ac.kaist.safe.nodes.cfg.{CFG, CFGFunction, CFGId, Call, NormalBlock}
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.Span

class FunctionBuilder(
  forCfg: CFG,
  funcName: String,
  funcSpan: Span,
  isUser: Boolean = true,
  parentScope: Option[CfgVarsScope] = None,
) extends AbsBuilder {

  private var argName: String = _
  private var argsIdsList: List[CFGId] = List()
  private var localIdsList: List[CFGId] = List()
  private var bBuilders: List[AbsBlockBuilder] = List()


  private val fakeIr: IRNode = new FakeIRNodeBuilder().span(funcSpan).build()
  private val funcScope: CfgVarsScope = new CfgVarsScope(funcName, parentScope)


  def addNormalBlock(): NormalBlockBuilder = {
    val bBuilder = new NormalBlockBuilder()
  }

  def build(): CFGFunction = forCfg.createFunction(
    argName = argName, argVars = argsIdsList,
    localVars = localIdsList, name = funcName,
    ir = fakeIr, isUser = isUser
  )



}