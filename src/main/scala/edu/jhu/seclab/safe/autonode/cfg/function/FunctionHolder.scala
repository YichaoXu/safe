package edu.jhu.seclab.safe.autonode.cfg.function

import edu.jhu.seclab.safe.autonode.cfg.abs.{AbsBlockHolder, AbsMutableHolder}
import edu.jhu.seclab.safe.autonode.query.csv.model.ModelNode
import edu.jhu.seclab.safe.autonode.query.csv.model.NodeType.{AST_FUNC_DECL, AST_TOP_LEVEL}

import scala.collection.mutable.ListBuffer;

class FunctionHolder(val funDef: ModelNode) extends AbsMutableHolder[AbsBlockHolder]{

  private val holderList = ListBuffer[AbsBlockHolder]()
  def blocks: Seq[AbsBlockHolder] = holderList
  def exist(anyBlockSatisfied: AbsBlockHolder => Boolean): Boolean = holderList.exists(anyBlockSatisfied)
  def funcName: String = funDef.node_type match {
    case AST_TOP_LEVEL => "top-level"
    case AST_FUNC_DECL => funDef.code
  }

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