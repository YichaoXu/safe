package edu.jhu.seclab.safe.autonode.exts

import slick.dbio.Effect
import slick.jdbc.{ GetResult, SQLActionBuilder }
import slick.sql.SqlStreamingAction

object database {
  implicit class mapResult(self: SQLActionBuilder) {
    type CsvMap = Map[String, String]
    def resultAsCsvMap: SqlStreamingAction[Vector[CsvMap], CsvMap, Effect] =
      self.as[CsvMap](GetResult[CsvMap] { prs =>
        (1 to prs.numColumns).map(_ => prs.rs.getMetaData.getColumnName(prs.currentPos + 1) -> prs.nextString).toMap
      })
  }
}
