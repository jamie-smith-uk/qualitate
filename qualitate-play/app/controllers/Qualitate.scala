package uk.co.qualitate

import play.api.Logger
import slick.driver.PostgresDriver
import slick.jdbc.meta.MTable

import scala.concurrent.{Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import slick.driver.PostgresDriver.api._

import scala.util.{Success, Failure}

// The main application
object Qualitate extends App {
  try {
    Logger.debug("Starting Up ...")

    setUpSchema(NativeAdvertsDAO.schema)

    val outbrainNativeAdvertsList = new ExcelReader(OutbrainDataAdapter).readFile("C:\\work\\qualitate\\Files\\OutBrain.xlsx")
    val taboolaNativeAdvertsList = new ExcelReader(TaboolaDataAdapter).readFile("C:\\work\\qualitate\\Files\\Taboola.xlsx")
    val contentClickNativeAdvertsList = new ExcelReader(ContentClickDataAdapter).readFile("C:\\work\\qualitate\\Files\\ContentClick.xlsx")

    DataAccess.addNativeAdverts(outbrainNativeAdvertsList)
    DataAccess.addNativeAdverts(taboolaNativeAdvertsList)
    DataAccess.addNativeAdverts(contentClickNativeAdvertsList)
  }
  catch {
    case e: Throwable =>  Logger.error(e.getMessage)
  } finally {
    Logger.debug("Closing Down ...")
    DataAccess.dataconnection.close
  }

  def setUpSchema(schema: PostgresDriver.SchemaDescription)={
    DataAccess.dropSchema(schema)
    DataAccess.createSchema(schema)
  }
}