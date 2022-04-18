package edu.jhu.mssi.seclab.extsafe.autonode.builder

import edu.jhu.mssi.seclab.extsafe.autonode.utils.CfgScope
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.{ModelNode, NodeType}
import kr.ac.kaist.safe.nodes.cfg.{CFGFunction, LabelKind, LoopHead, NormalBlock}

import scala.collection.mutable.ListBuffer

class NormalBlockBuilder(
  private val scope: CfgScope,
  private val label: LabelKind,
  private val outerLoop: Option[LoopHead] = None,
) extends AbsBlockBuilder {

  protected val blockScope: CfgScope = scope
  private val allocInstructions = ListBuffer[ModelNode]()

  def addAlloc(node: ModelNode): NormalBlockBuilder = _selfReturn{
    require(node.is(NodeType.AST_ASSIGN) )
    allocInstructions += node
  }

  override def build(forFunc: CFGFunction):NormalBlock = {
    val built = forFunc.createBlock(label, outerLoop)
    //TODO: FINISH INSTRUCTION

  }
}