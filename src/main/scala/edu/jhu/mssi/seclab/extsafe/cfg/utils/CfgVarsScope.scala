package edu.jhu.mssi.seclab.extsafe.cfg.utils

import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.NodeUtil.GLOBAL_PREFIX

class CfgVarsScope(
  val name: String,
  val parent: Option[CfgVarsScope]
) {

  private var localVariables: Map[String, CFGId] = Map()

  lazy val uniqueName: String = {
    if(this.parent isEmpty) s"$GLOBAL_PREFIX $name"
    else s"${this.parent.get.uniqueName}.$name"
  }

  // TODO: ADD FOR CAPTURE AND CAPTEDCAPTURE
  def getVarType: VarKind = {
    if (this.parent isEmpty) GlobalVar
    else PureLocalVar
  }

  def getVarCfgId(vName: String): Option[CFGId] =
    localVariables.get(vName).orElse {
      if(this.parent isEmpty) None
      else this.parent.get.getVarCfgId(vName)
    }

  // TODO: WE DID NOT IMPLEMENT WITH STATEMENT
  def newLocalVarCfgId(vName: String, isUser: Boolean=true): CFGId = {
    if (localVariables.contains(vName)) return localVariables(vName)
    val newLocVar: CFGId =
      if (!isUser) CFGTempId(s"${this.uniqueName}.$name", this.getVarType)
      else CFGUserId(s"${this.uniqueName}.$name", this.getVarType, name, fromWith = false)
    localVariables += (vName -> newLocVar)
    localVariables(vName)
  }
}
