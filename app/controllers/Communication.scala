package controllers

import akka.actor.{Actor, Props}
import play.api.Logger
import play.api.libs.iteratee.Concurrent
import play.libs.Akka

case class Start(out: Concurrent.Channel[String])
case class Send(message: String)


object Communication {



  def setup() = {

    class Listener extends Actor {

      var out = {
        val (enum, chan) = Concurrent.broadcast[String]
        chan
      }

      def receive = {
        //Websocket channel out is set here
        case Start(out) => {
          this.out = out
        }
        //Pushing messages to Websocket
        case Send(message) => {
          Logger.debug("Pushing Message: " + message)
          this.out.push(message)
        }
        case _ => {
          Logger.debug("receive with nothing ...")
        }
      }
    }

    val listener = Akka.system.actorOf(Props[Listener], "listener")
  }

  def sendtoclient(message:String) = {
    val listener = Akka.system().actorSelection("akka://application/user/listener")
    listener ! Send(message)
  }


}