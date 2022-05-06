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

import kr.ac.kaist.safe.analyzer.domain.Loc

// allocation site
sealed trait AllocSite extends Loc

// allocation site defined into user code
case class UserAllocSite(id: Int) extends AllocSite {
  override def toString: String = s"#$id"
}

// predefined allocation site
case class PredAllocSite(name: String) extends AllocSite {
  override def toString: String = s"#$name"
}
