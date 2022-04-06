package edu.jhu.mssi.seclab.extsafe.cfg.builder

abstract class AbsBuilder {
  protected def _selfReturn(f: => Unit): this.type = { f; this }
}
