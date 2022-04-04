package edu.jhu.mssi.seclab.extsafe.utils

import edu.jhu.mssi.seclab.extsafe.csv.entity.NodeType.{ AST_BREAK, AST_CATCH, AST_CONTINUE, AST_SWITCH, AST_SWITCH_CASE, AST_TRY, NodeType }
import edu.jhu.mssi.seclab.extsafe.csv.entity.{ Node, NodeType }
import kr.ac.kaist.safe.nodes.ast.Comment
import kr.ac.kaist.safe.nodes.cfg.{ CFGBin, CFGExpr, CaseLabel, CatchLabel, LabelKind, LoopBreakLabel, LoopContLabel, NoLabel, SwitchLabel, TryLabel }
import kr.ac.kaist.safe.util.Span

object CfgUtils {

  def isContained(out: Span, in: Span): Boolean = (out.begin <= in.begin) && (out.end >= in.end)

  def isSame(a: Span, b: Span): Boolean = (a.begin == b.begin) && (a.end == b.end)

  def type2Label(nType: NodeType): LabelKind = nType match {
    case AST_BREAK => LoopBreakLabel
    case AST_CONTINUE => LoopContLabel
    //case AST_BRANCH ???
    case AST_SWITCH => SwitchLabel
    case AST_SWITCH_CASE => CaseLabel
    //case AST_DEFAULT ???
    case AST_TRY => TryLabel
    // case AST_FINALLY??
    case AST_CATCH => CatchLabel
    case _ => NoLabel
  }

}
