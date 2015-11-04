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


class Application extends Controller {

  def index = Action {
    Ok(views.html.index(fileLoadRequest))
  }

  def fileLoadRequest = Form(
    mapping(
      "File Name" -> nonEmptyText,
      "Data Adapter" -> nonEmptyText
    )(FileLoadRequest.apply)(FileLoadRequest.unapply))

  def db = Action {

    try {
     setUpSchema(NativeAdvertsDAO.schema)

    }
    catch {
      case e: Throwable => Logger.error(e.getMessage)
    } finally {
      Logger.debug("Closing Down ...")
      DataAccess.dataconnection.close
    }
    Ok(views.html.index(fileLoadRequest))
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
      // remove this soon!
      setUpSchema(NativeAdvertsDAO.schema)
      val adverts = new ExcelReader(mapAdapter(adapter)).readFile(file)
      DataAccess.addNativeAdverts(adverts)
    }
    catch {
      case e: Throwable => Logger.error(e.getMessage)
    } finally {
      Logger.debug("Closing Down DB Connection")
      DataAccess.dataconnection.close
    }
  }

  def mapAdapter(adapterType:String): ExcelDataAdapter = adapterType match {
    case "Outbrain" => OutbrainDataAdapter
    case "Taboola" => TaboolaDataAdapter
    case "ContentClick" => ContentClickDataAdapter
  }

  def setUpSchema(schema: PostgresDriver.SchemaDescription) = {
    Logger.debug("Setting up Schema...")
    DataAccess.dropSchema(schema)
    DataAccess.createSchema(schema)
  }

}
