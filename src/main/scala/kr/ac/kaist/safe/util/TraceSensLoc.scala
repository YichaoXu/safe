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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.analyzer.TracePartition
import kr.ac.kaist.safe.analyzer.domain.Loc

////////////////////////////////////////////////////////////////////////////////
// trace sensitive location
////////////////////////////////////////////////////////////////////////////////
case class TraceSensLoc(
  loc: Loc,
  tp: TracePartition) extends Loc {
  override def toString: String = s"${loc}:Sens[${tp}]"
}
object TraceSensLoc {
  def apply(name: String, tp: TracePartition): TraceSensLoc =
    TraceSensLoc(PredAllocSite(name), tp)
}
