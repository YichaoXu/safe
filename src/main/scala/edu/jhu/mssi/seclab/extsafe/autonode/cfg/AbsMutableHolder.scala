package edu.jhu.mssi.seclab.extsafe.autonode.cfg

abstract class AbsMutableHolder[Content] extends AbsHolder {
  private var __isClosed = false
  def isClosed: Boolean = __isClosed
  def nonClosed: Boolean = !__isClosed
  def close(): Unit = this.__isClosed = true

  def +=(newVal: Content): Unit
  def :+=(newVal: Content): AbsMutableHolder[Content]

  protected def _selfReturn(f: => Unit): this.type = { f; this }
}
