package edu.jhu.seclab.safe

import scala.util.Properties

object EnvVariable {
  val SAFE_HOME: String = Properties.envOrNone("SAFE_HOME").get
  val SAFE_BIN_PATH: String = s"$SAFE_HOME/bin"
  val SOLVER_HOME: String = Properties.envOrElse("SOLVER_HOME", "")
}
