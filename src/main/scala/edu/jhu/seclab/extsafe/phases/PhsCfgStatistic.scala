package edu.jhu.seclab.extsafe.phases

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer.domain.{ BoolDomain, DefaultBool, DefaultNull, DefaultNumber, DefaultUndef, NullDomain, NumDomain, StrDomain, StringSet, UndefDomain }
import kr.ac.kaist.safe.analyzer.{ CallSiteSensitivity, LoopSensitivity }
import kr.ac.kaist.safe.nodes.cfg.{ BlockId, CFG }
import kr.ac.kaist.safe.phase.{ Config, PhaseObj }
import kr.ac.kaist.safe.util.{ BoolOption, NumOption, OptionKind, StrOption }

import scala.util.{ Success, Try }
import scala.collection.mutable

case object PhsCfgStatistic extends PhaseObj[CFG, CfgStatisticConfig, StatisticOutput] {

  override val name: String = "PhsCfgStatistic"
  override val help: String = "output the statistic result of "
  override def defaultConfig: CfgStatisticConfig = CfgStatisticConfig()

  def apply(
    cfg: CFG, safeConfig: SafeConfig, config: CfgStatisticConfig): Try[StatisticOutput] = {
    val traversed = mutable.HashSet[BlockId]()
    val output = StatisticOutput(0, 0)

    cfg.getAllBlocks.foreach(block => if (!traversed.contains(block.id)) {
      println(s"BLOCK ${block.toString()} \n\t${block.span.fileName} \n\tFROM ${block.span.begin} \n\tTO ${block.span.end}")
      output.node_count += 1
      output.edge_count += block.getAllSucc.size
      traversed.add(block.id)
    })
    Success(output)
  }

  override val options: List[(String, OptionKind[CfgStatisticConfig], String)] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during heap building are muted."),
    ("maxStrSetSize", NumOption((c, n) => if (n > 0) c.AbsStr = StringSet(n)),
      "the analyzer will use the AbsStr Set domain with given size limit n."),
    ("recency", BoolOption(c => c.recencyMode = true),
      "analysis with recency abstraction."),
    ("heap-clone", BoolOption(c => c.heapClone = true),
      "analysis with heap cloning that divides locations based on the given trace sensitivity."),
    ("callsiteSensitivity", NumOption((c, n) => if (n >= 0) c.callsiteSensitivity = CallSiteSensitivity(n)),
      "{number}-depth callsite-sensitive analysis will be executed."),
    ("loopIter", NumOption((c, n) => if (n >= 0) c.loopSensitivity = c.loopSensitivity.copy(maxIter = n)),
      "{number}-iteration loop-sensitive analysis will be executed."),
    ("loopDepth", NumOption((c, n) => if (n >= 0) c.loopSensitivity = c.loopSensitivity.copy(maxDepth = n)),
      "{number}-depth loop-sensitive analysis will be executed."),
    ("snapshot", StrOption((c, s) => c.snapshot = Some(s)),
      "analysis with an initial heap generated from a dynamic snapshot(*.json)."))

}

case class StatisticOutput(var edge_count: Int, var node_count: Int)

case class CfgStatisticConfig(
  var silent: Boolean = false,
  var AbsUndef: UndefDomain = DefaultUndef,
  var AbsNull: NullDomain = DefaultNull,
  var AbsBool: BoolDomain = DefaultBool,
  var AbsNum: NumDomain = DefaultNumber,
  var AbsStr: StrDomain = StringSet(0),
  var callsiteSensitivity: CallSiteSensitivity = CallSiteSensitivity(0),
  var loopSensitivity: LoopSensitivity = LoopSensitivity(0, 0),
  var snapshot: Option[String] = None,
  var recencyMode: Boolean = false,
  var heapClone: Boolean = false) extends Config
