package uk.co.qualitate

import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import scala.concurrent.{Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.Logger

import scala.concurrent.Await
import scala.util.{Failure, Success}

object DataAccess {

  val dataconnection = Database.forConfig("pgdb")


  private def filterSchemaName(tableName: String, schemaName: String): Option[String] = {
    if (tableName.startsWith("MQName(" + schemaName)) Some(tableName) else None
  }

  def schemaExists(schemaName:String): Boolean ={
    val tables = Await.result(DataAccess.dataconnection.run(MTable.getTables), 1.second).toList
    !tables.flatMap(f => filterSchemaName(f.name.toString(), schemaName)).isEmpty
  }

  def dropSchema(schema: PostgresDriver.SchemaDescription) = {
    try {
      Await.result(DataAccess.dataconnection.run(schema.drop), 1.second)
    }
    catch {
      case e: Throwable => Logger.error(e.getMessage)
    }
  }

  def createSchema(schema: PostgresDriver.SchemaDescription) = {
    try {
      Await.result(DataAccess.dataconnection.run(NativeAdvertsDAO.schema.create), 1.second)
    }
    catch {
      case e: Throwable =>  Logger.error(e.getMessage)
    }
  }

  def addNativeAdverts(nativeAdverts:List[NativeAdvert]) = {
    val setup = DBIO.seq(
      NativeAdvertsDAO ++= nativeAdverts
    )
    Await.result(DataAccess.dataconnection.run(setup), 1.second)
  }


}
