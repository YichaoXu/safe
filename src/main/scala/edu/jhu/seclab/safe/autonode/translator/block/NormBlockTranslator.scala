package edu.jhu.seclab.safe.autonode.translator.block

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType._
import edu.jhu.seclab.safe.autonode.cfg.block.NormBlockHolder
import edu.jhu.seclab.safe.autonode.query.autonode.model.ModelNode
import edu.jhu.seclab.safe.autonode.query.safe.SafeCfg
import edu.jhu.seclab.safe.autonode.translator.InstructionTranslator
import kr.ac.kaist.safe.nodes.cfg._

class NormBlockTranslator extends AbsBlockTranslator[CFGBlock] {

  private var source: NormBlockHolder = _ // NULLABLE
  private var destination: CFGFunction = _ // NULLABLE

  def input(is: AbsBlockHolder): NormBlockTranslator = _selfReturn { this.source = is.asInstanceOf[NormBlockHolder] }
  def output(into: CFGFunction): NormBlockTranslator = _selfReturn { this.destination = into }

  private def translateNormalBlockWithInstructions(label: LabelKind): NormalBlock = {
    val newBlock = destination.createBlock(label, None)
    val instrTranslator = new InstructionTranslator().output(to = newBlock)
    SafeCfg.query.instructions(ofFuncName = destination.name)
      .filter { oldInst => source.shouldContain(oldInst) && !oldInst.isInstanceOf[CFGCallInst] }.reverse
      .foreach { oldInst => instrTranslator.input(oldInst).translate() }
    newBlock
  }

  private val translateConditional: PartialFunction[Option[ModelNode], Option[NormalBlock]] = {
    case Some(ifNode) if ifNode is AST_IF_ELEM =>
      translateNormalBlockWithInstructions(BranchLabel)
    case Some(switchNode) if source.last is AST_SWITCH_LIST =>
      translateNormalBlockWithInstructions(SwitchLabel)
    case Some(caseNode) if caseNode is AST_SWITCH_CASE =>
      translateNormalBlockWithInstructions(CaseLabel)
  }

  private val translateTryCatch: PartialFunction[Option[ModelNode], Option[NormalBlock]] = {
    case Some(tryNode) if source.last is AST_TRY =>
      translateNormalBlockWithInstructions(TryLabel)
    case Some(catchNode) if catchNode is AST_CATCH_LIST =>
      translateNormalBlockWithInstructions(CatchLabel)
  }
  private val translateTrivia: PartialFunction[Option[ModelNode], Option[CFGBlock]] = {
    case Some(node) if node.isFuncEntry => destination.entry
    case Some(node) if node.isFuncEnd => destination.exit
    case Some(node) if node.isBlockEnd => None
    case None => println(s"\n⚠️  A BLOCK CANNOT BE TRANSLATED ⚠️\n{${source.toString}}\n")
  }

  private val translateRemaining: PartialFunction[Option[ModelNode], Option[CFGBlock]] = {
    case Some(_) => translateNormalBlockWithInstructions(NoLabel)
  }

  private val translateCore =
    translateConditional.orElse(translateTryCatch).orElse(translateTrivia).orElse(translateRemaining)

  // TODO: OPTIMISATION FOR BINARY SEARCHING
  def translate(): Option[CFGBlock] = translateCore(source.head)
}
