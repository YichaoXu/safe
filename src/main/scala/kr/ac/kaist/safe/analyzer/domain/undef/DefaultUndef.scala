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

// default undefined abstract domain
object DefaultUndef extends UndefDomain {
  case object Top extends Elem
  case object Bot extends Elem

  def alpha(undef: Undef): Elem = Top

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Undef] = this match {
      case Bot => ConFin()
      case Top => ConFin(Undef)
    }

    def getSingle: ConSingle[Undef] = this match {
      case Bot => ConZero
      case Top => ConOne(Undef)
    }

    override def toString: String = this match {
      case Bot => "⊥(undefined)"
      case Top => "Top(undefined)"
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Top, Bot) => false
      case _ => true
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    def StrictEquals(that: Elem): AbsBool = (this, that) match {
      case (Top, Top) => AbsBool.True
      case _ => AbsBool.Bot
    }
  }
}
