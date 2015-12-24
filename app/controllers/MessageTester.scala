package uk.co.qualitate

import play.api.libs.iteratee._

class MessageTester (channel:Concurrent.Channel[String]){

  var numberOfMessages = 100

  def runMessageTest() = {

    while (numberOfMessages >0 ){
      numberOfMessages-=1
      channel push ("Messages Left: " + numberOfMessages)
    }


  }

}
