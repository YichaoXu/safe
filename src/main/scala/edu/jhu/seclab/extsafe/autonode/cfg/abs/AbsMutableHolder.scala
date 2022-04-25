package edu.jhu.seclab.extsafe.autonode.cfg.abs

abstract class AbsMutableHolder[Content] extends AbsHolder {
  private var __isClosed = false
  def isClosed: Boolean = __isClosed
  def nonClosed: Boolean = !__isClosed
  def close(): AbsMutableHolder[Content] = _selfReturn { this.__isClosed = true }

  def head: Option[Content]
  def last: Option[Content]

  def +=(newVal: Content): Unit
  def append(newVal: Content): AbsMutableHolder[Content]

  protected def _selfReturn(f: => Unit): this.type = { f; this }
}
