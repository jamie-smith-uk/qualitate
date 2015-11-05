package uk.co.qualitate.test

import org.scalatest.matchers.{MatchResult, Matcher}
import uk.co.qualitate._
import org.scalatest._

class NativeAdvertInfoTest  extends FlatSpec with Matchers {

  "A native advert" should "should initialise with blank and zero values" in {
    val nativeAdvert = new NativeAdvertInfo()
    nativeAdvert.title should be ("")
    nativeAdvert.clicks should be (0)
    nativeAdvert.cost.value should be (0.00)
    nativeAdvert.url should be ("")
    nativeAdvert.impressions should be (0)
  }

  it should  "map valid title correctly" in {
    val nativeAdvert = new NativeAdvertInfo()
    val title = "New Title"
    nativeAdvert.mapValue(NativeAdvertClassification.TITLE, title)
    nativeAdvert.title should be (title)
  }

  it should  "map valid clicks correctly" in {
    val nativeAdvert = new NativeAdvertInfo()
    val clicks = 101
    nativeAdvert.mapValue(NativeAdvertClassification.CLICKS, "101")
    nativeAdvert.clicks should be (clicks)
  }

  it should  "map valid cost correctly" in {
    val nativeAdvert = new NativeAdvertInfo()
    val price = new Price("£3.21")
    nativeAdvert.mapValue(NativeAdvertClassification.COST, "£3.21")
    nativeAdvert.cost.value should be (3.21)
    nativeAdvert.cost.symbol should be ("GBP")
  }

  it should  "map valid url correctly" in {
    val nativeAdvert = new NativeAdvertInfo()
    val url = "https://www.google.co.uk"
    nativeAdvert.mapValue(NativeAdvertClassification.URL, url)
    nativeAdvert.url should be (url)
  }

  it should  "map valid impressions correctly" in {
    val nativeAdvert = new NativeAdvertInfo()
    val imp = 202
    nativeAdvert.mapValue(NativeAdvertClassification.IMPRESSIONS, "202")
    nativeAdvert.impressions should be (imp)
  }

}

