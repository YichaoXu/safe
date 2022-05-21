package edu.jhu.seclab.safe.autonode.query.autonode

import edu.jhu.seclab.safe.autonode.query.autonode.model.EdgeType.EdgeType
import edu.jhu.seclab.safe.autonode.query.autonode.model.ModelNode

import java.io.File

class AutoNodeSql(sqlFile: File) extends AbsAutoNode {
  override def node(id: Int): Option[ModelNode] = ???

  override def next(of: ModelNode, eType: Option[EdgeType]): Seq[ModelNode] = ???

  override def prev(of: ModelNode, eType: Option[EdgeType]): Seq[ModelNode] = ???
}
