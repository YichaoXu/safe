package edu.jhu.seclab.safe.autonode.query.autonode

import edu.jhu.seclab.safe.autonode.exts.syntax._
import edu.jhu.seclab.safe.autonode.query.autonode.model.{ ModelEdge, ModelNode }
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType.{ AST_CLOSURE, STRING }
import edu.jhu.seclab.safe.autonode.query.autonode.model.EdgeType.{ ENTRY, EdgeType, FLOWS_TO, PARENT_OF }

abstract class AbsAutoNode {

  def fileEntry: Option[ModelNode]

  def node(id: Int): Option[ModelNode]
  def next(of: ModelNode, eType: Option[EdgeType] = None): Seq[ModelNode]
  def prev(of: ModelNode, eType: Option[EdgeType] = None): Seq[ModelNode]


  def flowFrom(target: ModelNode): Seq[ModelNode] = this.next(of = target, eType = FLOWS_TO)
  def flowTo(target: ModelNode): Seq[ModelNode] = this.prev(of = target, eType = FLOWS_TO)

  def signatureOf(entry: ModelNode): Option[ModelNode] = this.prev(of = entry, eType = ENTRY).headOption match {
    case Some(signature) if signature.is(AST_CLOSURE) && signature.code.isEmpty =>
      this.next(of = signature, PARENT_OF).find(_ is STRING).orElse(
        Range.inclusive(signature.id - 5, signature.id + 5)
          .map(id => this.node(id).get)
          .find(n => n.is(STRING) && n.name.nonEmpty)
      ) match {
          case Some(strNode) => new ModelNode(signature.fileName, signature.data.updated("code", strNode.name))
          case None => println(s"⚠️  CANNOT FOUND THE SIGNATURE NAME OF ${signature.toString} ⚠️")
        }
    case Some(signature) => signature
    case None => println(s"⚠️  CANNOT FOUND THE SIGNATURE OF ENTRY ${entry.toString} ⚠️")
  }

  def entryOf(signature: ModelNode): Option[ModelNode] = this.next(of = signature, eType = ENTRY).headOption

  def invocationsInChildrenOf(target: ModelNode): Seq[ModelNode] = {
    val children = this.next(of = target, PARENT_OF).flatMap(invocationsInChildrenOf)
    if (target nonInvocation) children
    else children :+ target
  }

}