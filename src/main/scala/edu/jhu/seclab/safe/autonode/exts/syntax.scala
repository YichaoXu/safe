package edu.jhu.seclab.safe.autonode.exts

object syntax {
  implicit def autoWrapToOption[T](x: T): Option[T] = Option[T](x)
  implicit def autoWrapUnitToNone(x: Unit): None.type = None
}
