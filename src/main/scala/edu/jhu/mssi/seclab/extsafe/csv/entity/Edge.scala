package edu.jhu.mssi.seclab.extsafe.csv.entity

import EdgeType.EdgeType

case class Edge(
  private val data: Map[String, String]) {
  val start: Int = data("start:START_ID").toInt
  val end: Int = data("end:END_ID").toInt
  val edge_var: String = data("var")
  val taint_src: String = data("taint_src")
  val taint_dst: String = data("taint_dst")

  private val edge_type: String = data("type:TYPE")

  def is(eType: EdgeType): Boolean = eType.toString eq edge_type

  def in(eTypes: EdgeType*): Boolean = eTypes.exists(eTypes => is(eTypes))
}
