package edu.jhu.seclab.safe.autonode.translator

import edu.jhu.seclab.safe.autonode.cfg.abs.AbsBlockHolder
import edu.jhu.seclab.safe.autonode.cfg.block.CallBlockHolder
import edu.jhu.seclab.safe.autonode.exts.cfg.CurryCallCreate
import edu.jhu.seclab.safe.autonode.exts.span.Comparison
import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.query.csv.model.ModelNode
import edu.jhu.seclab.safe.autonode.query.csv.model.NodeType._
import edu.jhu.seclab.safe.autonode.query.safecfg.SafeCfg
import kr.ac.kaist.safe.nodes.cfg.{Call => CallBlock, _}

class BlockTranslator extends AbsTranslator[CFGBlock] {

  private var source: AbsBlockHolder = _ // NULLABLE
  private var destination: CFGFunction = _ // NULLABLE

  def input(is: AbsBlockHolder): BlockTranslator = _selfReturn{ this.source = is }
  def output(into: CFGFunction):  BlockTranslator = _selfReturn{ this.destination = into }

  private def translateNormalBlockWithInstructions(label: LabelKind): NormalBlock = {
    val newBlock = destination.createBlock(label, None)
    val instrTranslator = new NormInstructionTranslator().output(to=newBlock)
    SafeCfg.query.instructions(ofFuncName=destination.name)
      .filter{oldInst => source.shouldContain(oldInst) && !oldInst.isInstanceOf[CFGCallInst]}.reverse
      .foreach{oldInst => instrTranslator.input(oldInst).translate()}
    newBlock
  }

  private val translateConditional: PartialFunction[Option[ModelNode], Option[NormalBlock]] = {
    case Some(ifNode) if ifNode is AST_IF_ELEM =>
      translateNormalBlockWithInstructions(BranchLabel)
    case Some(switchState) if source.last is AST_SWITCH_LIST =>
      translateNormalBlockWithInstructions(SwitchLabel)
    case Some(switchCase) if switchCase is AST_SWITCH_CASE =>
      translateNormalBlockWithInstructions(CaseLabel)
  }

  private val translateTryCatch:PartialFunction[Option[ModelNode], Option[NormalBlock]] = {
    case Some(_) if source.last is AST_TRY=>
      translateNormalBlockWithInstructions(TryLabel)
    case Some(node) if node is AST_CATCH_LIST=>
      translateNormalBlockWithInstructions(CatchLabel)
  }

  private val translateCall:PartialFunction[Option[ModelNode], Option[CallBlock]] = {
    case Some(callNode) if (callNode is AST_CALL) || source.isInstanceOf[CallBlockHolder] =>
      SafeCfg.query.calls(ofFuncName=destination.name).find(_.span.isCrossover(callNode.namespace)) match {
        case Some(oldCall) =>
          destination.createCallBlock(oldCall.afterCall.retVar)(newCall => newCall.callInst match {
            case call: CFGCall => call.copy(block = oldCall)
            case construct: CFGConstruct => construct.copy(block = oldCall)
          })
        case None => None
      }
  }

  private val translateTrivia: PartialFunction[Option[ModelNode], Option[CFGBlock]] = {
    case Some(node) if node.isFuncEntry => destination.entry
    case Some(node) if node.isFuncEnd => destination.exit
    case Some(node) if node.isBlockEnd => None
    case None => println(s"\n⚠️  A BLOCK CANNOT BE TRANSLATED ⚠️\n{${source.toString}}\n")
  }

  private val translateCore =
    translateConditional.orElse(translateTryCatch).orElse(translateCall).orElse(translateTrivia).orElse{
      case Some(_) => translateNormalBlockWithInstructions(NoLabel)
    }

  // TODO: OPTIMISATION FOR BINARY SEARCHING
  def translate(): Option[CFGBlock] = translateCore(source.head).asInstanceOf
}
