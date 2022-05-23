package edu.jhu.seclab.safe.autonode.query.autonode

import edu.jhu.seclab.safe.autonode.exts.database.mapResult
import edu.jhu.seclab.safe.autonode.query.autonode.model.EdgeType.EdgeType
import edu.jhu.seclab.safe.autonode.query.autonode.model.NodeType.AST_TOP_LEVEL
import edu.jhu.seclab.safe.autonode.query.autonode.model.ModelNode
import edu.jhu.seclab.safe.autonode.exts.syntax.autoWrapToOption
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import java.io.File
import scala.util.Try

class AutoNodeSql(sqlFile: File) extends AbsAutoNode {

  private val jsName = sqlFile.getName.replace(".db", "")
  private val dbCore = Database.forURL(s"jdbc:sqlite:${sqlFile.getAbsolutePath}", driver="org.sqlite.JDBC")

  private def __select_single_node(statement: SQLActionBuilder): Option[ModelNode] = {
    val result = Try{ Await.result(dbCore.run(statement.resultAsCsvMap),Duration.Inf) }
    if (result.isFailure || result.get.isEmpty) return None
    new ModelNode(jsName, result.get.head)
  }

  override def fileEntry: Option[ModelNode] = __select_single_node{
    sql"SELECT * FROM main.NodeTable WHERE id!=1 AND type=${AST_TOP_LEVEL.toString} LIMIT 1"
  }

  override def node(id: Int): Option[ModelNode] = __select_single_node{
    sql"SELECT * FROM main.NodeTable WHERE id=${id.toString}"
  }

  private def __select_all_nodes(statement: SQLActionBuilder): Seq[ModelNode] = {
    val result = Try{ Await.result(dbCore.run(statement.resultAsCsvMap),Duration.Inf) }
    if (result.isFailure || result.get.isEmpty) return Nil
    result.get.map(data=>new ModelNode(jsName, data))
  }

  override def next(of: ModelNode, eType: Option[EdgeType]): Seq[ModelNode] = __select_all_nodes(sql"""
    SELECT node.* FROM main.EdgeTable AS edge
        LEFT JOIN main.NodeTable AS node ON node.id = edge.end
    WHERE edge.start=${of.id}
        AND edge.type=${eType.getOrElse("NONE").toString}
  """)


  override def prev(of: ModelNode, eType: Option[EdgeType]): Seq[ModelNode] = __select_all_nodes(sql"""
    SELECT node.* FROM main.EdgeTable AS edge
        LEFT JOIN main.NodeTable AS node ON node.id = edge.start
    WHERE edge.end=${of.id}
        AND edge.type=${eType.getOrElse("NONE").toString}
  """)

}
