package edu.jhu.mssi.seclab.extsafe.autonode.builder

import kr.ac.kaist.safe.nodes.cfg.{ CFGBlock, CFGFunction }

abstract class AbsBlockBuilder extends AbsBuilder {
  def build(forFunc: CFGFunction): CFGBlock
}
