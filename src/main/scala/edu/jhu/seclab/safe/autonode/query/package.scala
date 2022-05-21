package edu.jhu.seclab.safe.autonode

import edu.jhu.seclab.safe.autonode.query.autonode.{ AbsAutoNode, AutoNodeCsv, AutoNodeSql }
import edu.jhu.seclab.safe.autonode.query.safe.SafeCfg
import edu.jhu.seclab.safe.autonode.exts.syntax.autoWrapToOption
import kr.ac.kaist.safe.nodes.cfg.CFG

import java.io.File

package object query {
  private var safeCfgCore: Option[SafeCfg] = None
  private var autoNodeCore: Option[AbsAutoNode] = None

  def safeCfg: SafeCfg = safeCfgCore.get
  def autoNode: AbsAutoNode = autoNodeCore.get

  def sourceOfAutoNode(nFile: File, eFile: File): Unit = {
    autoNodeCore = new AutoNodeCsv(nodeFile = nFile, edgeFile = eFile)
  }

  def sourceOfAutoNode(sqlFile: File): Unit = {
    autoNodeCore = new AutoNodeSql(sqlFile)
  }

  def sourceOfSafeCfg(cfg: CFG): Unit = {
    safeCfgCore = new SafeCfg(cfg)
  }
}
