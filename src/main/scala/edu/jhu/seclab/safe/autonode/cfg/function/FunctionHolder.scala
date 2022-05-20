package edu.jhu.seclab.safe.autonode.cfg.function

import edu.jhu.seclab.safe.autonode.cfg.abs.{AbsBlockHolder, AbsMutableHolder}
import edu.jhu.seclab.safe.autonode.cfg.block.{CallBlockHolder, NormBlockHolder}
import edu.jhu.seclab.safe.autonode.query.csv.model.ModelNode
import edu.jhu.seclab.safe.autonode.query.csv.model.NodeType.{AST_CLOSURE, AST_FUNC_DECL, AST_TOP_LEVEL}

import scala.collection.mutable.ListBuffer;

class FunctionHolder(val funDef: ModelNode) extends AbsMutableHolder[AbsBlockHolder]{

  private val holderList = ListBuffer[AbsBlockHolder]()
  def blocks: Seq[AbsBlockHolder] = holderList
  def normBlocks: Seq[NormBlockHolder] = holderList.filter(_.isInstanceOf[NormBlockHolder]).map(_.asInstanceOf[NormBlockHolder])
  def callBlocks: Seq[CallBlockHolder] = holderList.filter(_.isInstanceOf[CallBlockHolder]).map(_.asInstanceOf[CallBlockHolder])
  def exist(anyBlockSatisfied: AbsBlockHolder => Boolean): Boolean = holderList.exists(anyBlockSatisfied)

  def funcName: String = funDef.node_type match {
    case AST_TOP_LEVEL => "top-level"
    case AST_FUNC_DECL | AST_CLOSURE => funDef.code
  }

  override def head: AbsBlockHolder = holderList.head
  override def last: AbsBlockHolder = holderList.last
  override def +=(newVal: AbsBlockHolder): Unit = this append newVal
  override def append(newVal: AbsBlockHolder): FunctionHolder = this._selfReturn{ holderList += newVal }

  override def toString: String = {
    val sBuilder = new StringBuilder(s"FUNCTION: \tcode: ${funDef.code}; \tnamespace: ${funDef.namespace}\n\n")
    holderList.foreach{holder => sBuilder ++= s"${holder.toString}\n"}
    sBuilder.toString()
  }

}