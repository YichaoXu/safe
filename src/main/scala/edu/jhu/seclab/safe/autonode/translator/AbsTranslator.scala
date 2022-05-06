package edu.jhu.seclab.safe.autonode.translator

abstract class AbsTranslator[Target]{
  protected def _selfReturn(f: => Unit): this.type = { f; this }
  def translate(): Option[Target]
}
