package edu.jhu.mssi.seclab.extsafe.autonode.cfg.function

import edu.jhu.mssi.seclab.extsafe.autonode.cfg.AbsMutableHolder
import edu.jhu.mssi.seclab.extsafe.autonode.cfg.block.AbsBlockHolder
import edu.jhu.mssi.seclab.extsafe.autonode.csv.model.ModelNode

import scala.collection.mutable.ListBuffer;

class FunctionHolder(val funDef: ModelNode) extends AbsMutableHolder[AbsBlockHolder]{

  private val holderList = ListBuffer[AbsBlockHolder]()

  def blocks: Seq[AbsBlockHolder] = holderList

  def exist(anyBlockSatisfied: AbsBlockHolder => Boolean): Boolean = holderList.exists(anyBlockSatisfied)

  override def +=(newVal: AbsBlockHolder): Unit = this :+= newVal
  override def :+=(newVal: AbsBlockHolder): FunctionHolder = this._selfReturn{ holderList += newVal }

}