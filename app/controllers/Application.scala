package controllers

import models.FileLoadRequest
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import slick.driver.PostgresDriver
import uk.co.qualitate._
import slick.driver.PostgresDriver.api._
import play.api.Logger
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext


class Application extends Controller {

  def index = Action {
    Ok(views.html.index(fileLoadRequest))
  }

  def fileLoadRequest = Form(
    mapping(
      "File Name" -> nonEmptyText,
      "Data Adapter" -> nonEmptyText
    )(FileLoadRequest.apply)(FileLoadRequest.unapply))


  def createSchema = Action {
    try {
     setUpSchema(NativeAdvertsDAO.schema)
    }
    catch {
      case e: Throwable => Logger.error(e.getMessage)
        Ok(s"Schema Creation Failed")
    }
    Ok(s"Schema Created")
  }


  def getNativeAdverts = Action.async {
    val futureAds = DataAccess.asyncGetNativeAdverts()
    futureAds.map(f => {
      var resultsString = "Got " + f.length + " results."
      f.foreach{na => resultsString += na.toString }
      Ok(resultsString)
    }
    )
  }


  def createCustomer = Action { implicit request => {
    fileLoadRequest.bindFromRequest().fold(
      formWithErrors => {
        BadRequest(views.html.index(formWithErrors))
      },
      fileRequest => {
        processFileRequest(fileRequest.location, fileRequest.adapterType)
        Ok(s"File Requested: ${fileRequest.location} , Adapter Requested: ${fileRequest.adapterType}")
      }
    )
  }
  }

  def processFileRequest(file:String, adapter:String) ={

    try {
      val adverts = new ExcelReader(mapAdapter(adapter)).readFile(file)
      DataAccess.addNativeAdverts(adverts)
    }
    catch {
      case e: Throwable => Logger.error(e.getMessage)
    }
  }

  def mapAdapter(adapterType:String): ExcelDataAdapter = adapterType match {
    case "Outbrain" => OutbrainDataAdapter
    case "Taboola" => TaboolaDataAdapter
    case "ContentClick" => ContentClickDataAdapter
  }

  def setUpSchema(schema: PostgresDriver.SchemaDescription) = {
    DataAccess.dropSchema(schema)
    DataAccess.createSchema(schema)
  }

}
