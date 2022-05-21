package edu.jhu.seclab.safe.autonode.translator

abstract class AbsTranslator[Target] {

  type InputType
  type DestinationType

  def translate(): Option[Target]
  def input(is: InputType): AbsTranslator[Target]
  def output(into: DestinationType): AbsTranslator[Target]

  protected def _selfReturn(f: => Unit): this.type = { f; this }

}
