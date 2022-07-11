package edu.jhu.seclab.safe.autonode.cfg.abs

abstract class AbsMutableHolder[Content] extends AbsHolder {

  private var __isClosed = false

  def isClosed: Boolean = this.__isClosed
  def nonClosed: Boolean = !this.__isClosed
  def close(): AbsMutableHolder[Content] = _selfReturn { this.__isClosed = true }

  def head: Content
  def last: Content

  def +=(newVal: Content): Unit
  def append(newVal: Content): AbsMutableHolder[Content]

  protected def _selfReturn(f: => Unit): this.type = { f; this }
}
