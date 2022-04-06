package edu.jhu.mssi.seclab.extsafe.cfg.builder

import edu.jhu.mssi.seclab.extsafe.cfg.entity.{Node, NodeType}
import edu.jhu.mssi.seclab.extsafe.cfg.utils.CfgVarsScope
import kr.ac.kaist.safe.nodes.cfg.{CFGAlloc, CFGFunction, LabelKind, LoopHead, NormalBlock}

class NormalBlockBuilder(
  private val forFunc: CFGFunction,
  private val funcScope: CfgVarsScope,
  private val label: LabelKind,
  private val outerLoop: Option[LoopHead] = None,
) extends AbsBlockBuilder {

  private var allocInstructions: List[CFGAlloc] = Nil
  def addAlloc(node: Node): NormalBlockBuilder = _selfReturn{
    require(node.nodeType == NodeType.AST_ASSIGN)
    CFGAlloc()
  }

  override def build(forFunc: CFGFunction):NormalBlock = {
    val built = forFunc.createBlock(label, outerLoop)
    //TODO: FINISH INSTRUCTION
    built
  }
}