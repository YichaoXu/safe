package edu.jhu.mssi.seclab.extsafe.cfg.builder

abstract class AbstractBuilder[E] {
  protected def _selfReturn(f: => Unit): this.type = { f; this }
  def build(): Option[E]
}
