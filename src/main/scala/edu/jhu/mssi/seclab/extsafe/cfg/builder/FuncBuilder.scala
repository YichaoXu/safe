package edu.jhu.mssi.seclab.extsafe.cfg.builder
import edu.jhu.mssi.seclab.extsafe.csv.entity.{ Edge, Node }
import edu.jhu.mssi.seclab.extsafe.utils.CfgUtils
import edu.jhu.mssi.seclab.extsafe.utils.Syntax.findLastFrom
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock, CFGEdgeNormal, CFGFunction, Call, LoopHead }
import sun.reflect.generics.reflectiveObjects.NotImplementedException

class FuncBuilder(
  private val name: String,
  private val newCfg: CFG,
  private val preCfg: CFG
) extends AbstractBuilder[CFGFunction] {

  private val preFunc: CFGFunction = preCfg.getAllFuncs.find(f =>
    f.name equals name
  ).get
  private val newFunc: CFGFunction = newCfg.createFunction(
    preFunc.argumentsName, preFunc.argVars,
    preFunc.localVars, name, preFunc.ir,
    preFunc.isUser)

  private def findFromPreBlocks[WithType <: CFGBlock](node: Node): Option[WithType] = {
    val outBlock = preFunc.getAllBlocks.find { block =>
      block.isInstanceOf[WithType] && CfgUtils.isSame(node.namespace, block.span)
    }
    if (outBlock isEmpty) return None
    Some(outBlock.get.asInstanceOf[WithType])
  }

  private var idBlockMap: Map[Int, CFGBlock] = Map()
  private def LoopHeaders = idBlockMap.values
    .filter(_.isInstanceOf[LoopHead])
    .map(_.asInstanceOf[LoopHead])
  private def findOutLoop(node: Node): Option[LoopHead] = findLastFrom(LoopHeaders) {
    loop => CfgUtils.isContained(out = loop.span, in = node.namespace)
  }

  def addLoopHeaderFromPre(node: Node): FuncBuilder = _selfReturn {
    val preLoop = findFromPreBlocks[LoopHead](node)
    val newLoop = newFunc.createLoopHead(findOutLoop(node), preLoop.get.cond, node.namespace)
    idBlockMap += (node.id -> newLoop)
  }
  def addLoopHeader(node: Node, exprNodes: List[Node]): FuncBuilder = _selfReturn {
    //TODO: MAKE IT MORE RELIABLE
    throw new NotImplementedException()
  }

  def addBlock(node: Node): FuncBuilder = _selfReturn {
    val newBloc = newFunc.createBlock(CfgUtils.type2Label(node.nodeType), findOutLoop(node))
    idBlockMap += (node.id -> newBloc)
  }

  def addCall(node: Node): FuncBuilder = _selfReturn {
    val preCall = findFromPreBlocks[Call](node)
    val newCall = newFunc.createCall(
      (any: Call) => preCall.get.callInst,
      preCall.get.afterCall.retVar,
      findOutLoop(node))
    idBlockMap += (node.id -> newCall)
  }

  def addLink(link: Edge): FuncBuilder = _selfReturn {
    val fromNode = idBlockMap(link.start)
    val toNode = idBlockMap(link.end)
    //TODO: TYPE MODIFY
    fromNode.addSucc(CFGEdgeNormal, toNode)
    toNode.addSucc(CFGEdgeNormal, fromNode)
  }

  override def build(): Option[CFGFunction] = Some(newFunc)
}
