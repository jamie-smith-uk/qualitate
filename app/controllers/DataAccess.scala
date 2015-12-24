package uk.co.qualitate

import akka.actor.PoisonPill
import controllers.{Communication, Send, Start}
import play.libs.Akka
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.Logger
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
      Await.result(DataAccess.dataconnection.run(schema.drop), 20.second)
    }
    catch {
      case e: Throwable => Logger.error(e.getMessage)
    }
  }

  def createSchema(schema: PostgresDriver.SchemaDescription) = {
    try {
      Await.result(DataAccess.dataconnection.run(NativeAdvertsDAO.schema.create), 20.second)
    }
    catch {
      case e: Throwable =>  Logger.error(e.getMessage)
    }
  }

  def addNativeAdverts(nativeAdverts:List[NativeAdvert]) = {
    val setup = DBIO.seq(
      NativeAdvertsDAO ++= nativeAdverts
    )
    Await.result(DataAccess.dataconnection.run(setup), 20.second)
  }


  def asyncAddNativeAdverts(nativeAdverts:List[NativeAdvert]) = {
    val setup = DBIO.seq(
      NativeAdvertsDAO ++= nativeAdverts
    )

    val res: Future[Unit] = DataAccess.dataconnection.run(setup)

    res onComplete{
      case Success(value) => {
        Logger.debug("Native Adverts added.  Pushing Message ...")
        Communication.sendtoclient("Native Adverts successfully uploaded")
      }
      case Failure(e) => {
        Logger.error(e.getMessage)
        Communication.sendtoclient("Could not add Native Adverts")
      }
    }

  }


  def asyncGetNativeAdverts(): Future[Seq[NativeAdvert]] = {
    DataAccess.dataconnection.run(NativeAdvertsDAO.result)
  }

  def getNativeAdverts() = {
    val queryResultFuture: Future[Seq[NativeAdvert]] = DataAccess.dataconnection.run(NativeAdvertsDAO.result)

    queryResultFuture.onSuccess { case s => {
      Logger.debug("There are " + s.length + " adverts found ...")
      s.foreach{ f => Logger.debug(f.toString)  }
      }
    }
  }
}
