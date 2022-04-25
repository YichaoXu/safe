package edu.jhu.seclab.extsafe.autonode.cfg.function

import edu.jhu.seclab.extsafe.autonode.cfg.abs.AbsMutableHolder
import edu.jhu.seclab.extsafe.autonode.cfg.block.AbsBlockHolder
import edu.jhu.seclab.extsafe.autonode.query.csv.model.ModelNode

import scala.collection.mutable.ListBuffer;

class FunctionHolder(val funDef: ModelNode) extends AbsMutableHolder[AbsBlockHolder]{

  private val holderList = ListBuffer[AbsBlockHolder]()
  def blocks: Seq[AbsBlockHolder] = holderList
  def exist(anyBlockSatisfied: AbsBlockHolder => Boolean): Boolean = holderList.exists(anyBlockSatisfied)

  override def head: Option[AbsBlockHolder] = holderList.headOption
  override def last: Option[AbsBlockHolder] = holderList.lastOption
  override def +=(newVal: AbsBlockHolder): Unit = this append newVal
  override def append(newVal: AbsBlockHolder): FunctionHolder = this._selfReturn{ holderList += newVal }

  override def toString: String = {
    val sBuilder = new StringBuilder(s"FUNCTION: \tcode: ${funDef.code}; \tnamespace: ${funDef.namespace}\n\n")
    holderList.foreach{holder => sBuilder ++= s"${holder.toString}\n"}
    sBuilder.toString()
  }

}