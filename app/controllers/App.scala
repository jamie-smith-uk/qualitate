package controllers

import akka.actor._
import java.io.File
import java.nio.file.{Path, Files}

import models.FileLoadRequest
import play.api._
import play.api.libs.iteratee.{Enumerator, Iteratee, Concurrent}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.libs.Akka
import slick.driver.PostgresDriver
import uk.co.qualitate._
import slick.driver.PostgresDriver.api._
import play.api.Logger
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext


case class Specs(adapter: String)

class App extends Controller {

  val system = ActorSystem("Qualitate")
  Communication.setup()

  def index = Action {
    Ok(views.html.index(fileLoadRequest))
  }

  def fileLoadRequest = Form(
    mapping(
    "fileName" -> nonEmptyText,
      "dataAdapter" -> nonEmptyText
    )(FileLoadRequest.apply)(FileLoadRequest.unapply))


  val uploadForm = Form(
  mapping (
   "dataAdapter" -> nonEmptyText
  )(Specs.apply)(Specs.unapply))



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

  def createCustomer = Action(parse.multipartFormData) { implicit request => {

    val sp: Option[Specs] = uploadForm.bindFromRequest().fold(
      errFrm => None,
      spec => Some(spec)
    )

    request.body.file("fileName").map { f =>
      sp.map { spec =>
        import java.io.File
        val filename = f.filename
        val contentType = f.contentType

        Logger.debug("uploading file ... " + filename )

        val uploadFile = File.createTempFile(s"uploadedFile", ".tmp", new File("/tmp/files"))
        val fileThing = f.ref.moveTo(uploadFile, true)
        processFileRequest(fileThing, spec.adapter)

        // response
        Ok(views.html.result("File Uploaded")(s"File Upload Requested"))

      }.getOrElse {
        BadRequest("Form Binding Error")
      }
    }.getOrElse {
      BadRequest("File not attached")
    }
  }
  }

  def testMessage = Action {
    Logger.debug("testMessage Action called")
    Ok(views.js.testmessage.render())
  }

  /*
   * WebSocket Stuff
   */
  def messageSocket = WebSocket.using[String] { request =>

    val (out,channel) = Concurrent.broadcast[String]

    // start up the listener with the channel
    val listener = Akka.system().actorSelection("akka://application/user/listener")
    listener ! Start(channel)

    val in = Iteratee.foreach[String] {
      msg =>
        Logger.debug(msg)
    }
    (in,out)
  }


  def processFileRequest(file:File, adapter:String) ={
    try {
      DataAccess.asyncAddNativeAdverts(new ExcelReader(mapAdapter(adapter)).readFile(file))
    }
    catch {
      case e: Throwable => {
        Logger.error(e.getMessage)
       Communication.sendtoclient("Could not add Native Adverts")
      }
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
