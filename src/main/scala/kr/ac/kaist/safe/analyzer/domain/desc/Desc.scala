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

package kr.ac.kaist.safe.analyzer.domain

// concrete descriptor type
case class Desc(
  value: Option[Value],
  writable: Option[Bool],
  enumerable: Option[Bool],
  configurable: Option[Bool])
