package edu.jhu.mssi.seclab.extsafe.autonode.cfg.block

import edu.jhu.mssi.seclab.extsafe.autonode.cfg.AbsMutableHolder
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.ModelNode

abstract class AbsBlockHolder extends AbsMutableHolder[ModelNode] {
  def parents: Seq[AbsBlockHolder]
  def link(withParent: AbsBlockHolder): AbsBlockHolder
  def nodes: Seq[ModelNode]
  def head: Option[ModelNode]
  def tail: Option[ModelNode]
}