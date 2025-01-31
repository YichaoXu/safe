/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject into license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.FunctionId

// default internal value abstract domain
object DefaultIValue extends IValueDomain {
  lazy val Bot: Elem = Elem(AbsValue.Bot, AbsFId.Bot)
  lazy val Top: Elem = Elem(AbsValue.Top, AbsFId.Top)

  def alpha(value: IValue): Elem = value match {
    case (value: Value) => AbsValue(value)
    case (fid: FId) => AbsFId(fid)
  }

  def apply(value: AbsValue): Elem = Bot.copy(value = value)
  def apply(fidset: AbsFId): Elem = Bot.copy(fidset = fidset)
  def apply(value: AbsValue, fidset: AbsFId): Elem = Elem(value, fidset)

  case class Elem(value: AbsValue, fidset: AbsFId) extends ElemTrait {
    def gamma: ConSet[IValue] = ConInf // TODO more precisely

    def getSingle: ConSingle[IValue] = ConMany

    override def toString: String = {
      if (isBottom) "⊥Elem"
      else {
        var list: List[String] = Nil
        value.foldUnit(list :+= _.toString)
        fidset.foldUnit(list :+= _.toString)
        list.mkString(", ")
      }
    }

    def ⊑(that: Elem): Boolean = {
      val (left, right) = (this, that)
      left.value ⊑ right.value &&
        left.fidset ⊑ right.fidset
    }

    def ⊔(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value ⊔ right.value,
        left.fidset ⊔ right.fidset)
    }

    def ⊓(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value ⊓ right.value,
        left.fidset ⊓ right.fidset)
    }

    def copy(
      value: AbsValue,
      fidset: AbsFId): Elem = Elem(value, fidset)
  }
}
