package uk.co.qualitate

import play.api.Logger


class NativeAdvertInfo {

  var title: String = ""
  var clicks: Int = 0
  var cost: Price = new Price("0.00")
  var url: String = ""
  var impressions: Int = 0

  def mapValue(property:NativeAdvertClassification.Data, value:String) = {
    property match{
      case NativeAdvertClassification.CLICKS => clicks = value.toInt
      case NativeAdvertClassification.COST => cost = new Price(value)
      case NativeAdvertClassification.IMPRESSIONS => impressions= value.toInt
      case NativeAdvertClassification.TITLE => title = value.toString
      case NativeAdvertClassification.URL => url = value.toString
      case _ =>
    }
  }

  def toNativeAdvert():NativeAdvert ={
    val advert = new NativeAdvert(0, title, clicks, cost.value, cost.symbol, url, impressions)
    advert
  }

  override def toString(): String = "\r\nTitle:" + title + ", \r\nClicks:" + clicks + "\r\nCost: " + cost + "\r\nURL: " + url + "\r\nImpressions: " + impressions;
}




object NativeAdvertClassification extends Enumeration {
  type Data = Value
  val TITLE, CLICKS, COST, URL, IMPRESSIONS, NONE = Value
}

class Price (val price: String) {

  var value: Double = 0.00
  var symbol: String = ""
  try {
    if (price.contains('£')) {
      symbol = "GBP"
      value = price.split('£')(1).replaceAll("[^\\d.]", "").toDouble
    }
    else if (price.contains('$')) {
      symbol = "USD"
      value = price.split('$')(1).replaceAll("[^\\d.]", "").toDouble
    }
    else {
      // assume that it's GBP with no CCY symbol ...
      value = price.replaceAll("[^\\d.]", "").toDouble
      symbol = "GBP"
    }
  }
  catch {
    case e: Throwable => {
      Logger.error(e.getMessage)
      throw e
    }
  }

  override def toString(): String = "CCY: " + price + ", Value: " + value;
}




