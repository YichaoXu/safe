package edu.jhu.mssi.seclab.extsafe.autonode.utils

import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.NodeUtil.GLOBAL_PREFIX

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

sealed class CfgScope protected (
  val name: String,
  val parent: Option[CfgScope]
) {
  private val children = ListBuffer[CfgScope]()

  private val tempVariables = ListBuffer[CFGTempId]()
  private val localVariables = mutable.Map[String, CFGUserId]()

  // TODO: ADD FOR CAPTURE AND CAPTURED
  def getVarType: VarKind = {
    if (this.parent isEmpty) GlobalVar
    else PureLocalVar
  }

  def getCfgId(byName: String): Option[CFGId] =
    localVariables.get(byName).orElse {
      if (this.parent isEmpty) None
      else this.parent.get.getCfgId(byName)
    }

  // TODO: WE DID NOT IMPLEMENT WITH STATEMENT
  def addVarCfgId(vName: String): CFGUserId = {
    if (localVariables.contains(vName)) return localVariables(vName)
    localVariables(vName) = CFGUserId(vName, this.getVarType, name, fromWith = false)
    localVariables(vName)
  }

  def addTempId(): CFGTempId = {
    tempVariables += CFGTempId(s"<>temp<>${CfgScope.newUniquePostfix}", this.getVarType)
    tempVariables.last
  }

  def isUnder(scope: CfgScope): Boolean = {
    if(this == scope) return true
    if(parent isEmpty) return false
    parent.get.isUnder(scope)
  }

  def newSubScope(name: String): CfgScope = {
    val subScope = new CfgScope(name, Some(this))
    this.children += subScope
    subScope
  }

  def getAllChildren: List[CfgScope] = children.toList
  def getLocalVariables: List[CFGUserId] = this.localVariables.values.toList

}

object CfgScope {
  private var variableCounter = 0

  def newUniquePostfix: Int = {
    variableCounter += 1
    variableCounter
  }
}

class FunctionScope (
  private val name: String,
  private val parent: Option[CfgScope] = None
) extends CfgScope(name, parent) {

  private val arguments = ListBuffer[CFGTempId]()

  def addArgCfgId(name: String): CFGTempId = {
    val aName = s"<>$name<>${CfgScope.newUniquePostfix}"
    this.arguments += CFGTempId(aName, this.getVarType)
    this.arguments.last
  }

  def getArgCfgId(byName: String): Option[CFGTempId] =
    this.arguments.find(_.text.startsWith(s"<>$byName<>"))

  def getArgCfgIds: List[CFGTempId] =
    this.arguments.toList

  override def getCfgId(byName: String): Option[CFGId] =
    super.getCfgId(byName) orElse this.getArgCfgId(byName)

}


