package edu.jhu.seclab.safe.autonode.query.autonode.model

abstract sealed class AbsType extends Enumeration

object NodeType extends AbsType {
  type NodeType = Value
  val AST_TOP_LEVEL: Value = Value("AST_TOPLEVEL")
  val STRING: Value = Value("STRING")
  val AST_FUNC_DECL, CFG_FUNC_ENTRY, CFG_FUNC_EXIT, AST_STMT_LIST, AST_ASSIGN, AST_VAR, AST_NEW = Value
  val AST_METHOD_CALL, AST_CALL, AST_NAME, AST_ARG_LIST, AST_IF, AST_IF_ELEM, AST_BINARY_OP, AST_POST_DEC, AST_RETURN = Value
  val AST_TRY, AST_CATCH_LIST, AST_CATCH, AST_NAME_LIST, AST_SWITCH, AST_SWITCH_LIST, AST_SWITCH_CASE, AST_WHILE = Value
  val AST_CONTINUE, AST_BREAK, AST_FOR, AST_EXPR_LIST, AST_POST_INC, BASE_SCOPE, OBJECT, BOOLEAN, FILE, NULL, INTEGER = Value
  val AST_CLOSURE, AST_PARAM_LIST, DUMMY_STMT, FUNCTION, ARRAY, NUMBER, UNDEFINED, FILE_SCOPE, BLOCK_SCOPE, CFG_BLOCK_EXIT = Value
  def apply(str: String): Option[Value] = values.find(_.toString == str.toUpperCase())
}

object EdgeType extends AbsType {
  type EdgeType = Value
  val NULL, FILE_OF, ENTRY, EXIT, PARENT_OF, CALLS, FLOWS_TO, REFERS_TO, SCOPE_TO_VAR, PARENT_SCOPE_OF, NAME_TO_OBJ, OBJ_TO_PROP, OBJ_TO_AST, OBJ_TO_SCOPE, SCOPE_TO_AST = Value

  def apply(str: String): Option[Value] = values.find(_.toString == str)

}
