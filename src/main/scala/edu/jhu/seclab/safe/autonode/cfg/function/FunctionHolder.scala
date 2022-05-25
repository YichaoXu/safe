package edu.jhu.seclab.safe.autonode.cfg.function

import edu.jhu.seclab.safe.autonode.exts.span.Offset
import edu.jhu.seclab.safe.autonode.query.autonode.model.SignatureNode
import edu.jhu.seclab.safe.autonode.cfg.abs.{ AbsBlockHolder, AbsMutableHolder }
import edu.jhu.seclab.safe.autonode.cfg.block.{ CallBlockHolder, NormBlockHolder }
import kr.ac.kaist.safe.util.Span

import scala.collection.mutable.ListBuffer

class FunctionHolder(val signature: SignatureNode) extends AbsMutableHolder[AbsBlockHolder] {

  private val holderList = ListBuffer[AbsBlockHolder]()
  def blocks: Seq[AbsBlockHolder] = holderList
  def normBlocks: Seq[NormBlockHolder] = holderList.filter(_.isInstanceOf[NormBlockHolder]).map(_.asInstanceOf[NormBlockHolder])
  def callBlocks: Seq[CallBlockHolder] = holderList.filter(_.isInstanceOf[CallBlockHolder]).map(_.asInstanceOf[CallBlockHolder])
  def exist(anyBlockSatisfied: AbsBlockHolder => Boolean): Boolean = holderList.exists(anyBlockSatisfied)

  val funcName: String = signature.funcName
  val namespace: Span = signature.core.namespace.column(offset = 1)

  override def head: AbsBlockHolder = holderList.head
  override def last: AbsBlockHolder = holderList.last
  override def +=(newVal: AbsBlockHolder): Unit = this append newVal
  override def append(newVal: AbsBlockHolder): FunctionHolder = this._selfReturn { holderList += newVal }

  override def toString: String = {
    val sBuilder = new StringBuilder(s"FUNCTION: \tname: ${this.funcName}; \tnamespace: ${this.namespace}\n\n")
    holderList.foreach { holder => sBuilder ++= s"${holder.toString}\n" }
    sBuilder.toString()
  }

}