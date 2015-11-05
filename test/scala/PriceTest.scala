package uk.co.qualitate.test

import uk.co.qualitate._
import org.scalatest._

class PriceTest extends FlatSpec with Matchers {

  "A price" should "show GBP as the symbol when the price has £ at the start" in {
    val ccy = new Price("£0.00")
    ccy.symbol should be ("GBP")
  }

  it should  "show USD as the symbol when the price has $ at the start" in {
    val ccy = new Price("$0.00")
    ccy.symbol should be ("USD")
  }

  it should  "default to GBP if it can't find the currency symbol at the start" in {
    val ccy = new Price("0.00")
    ccy.symbol should be ("GBP")
  }

  it should  "translate a currency value of 0.00 into 0.00 as the value of the currency" in {
    val ccy = new Price("£0.00")
    ccy.value should be (0.00)
  }

  it should  "translate a  valid prive (£2.54) into the corresponding value (2.54)" in {
    val ccy = new Price("£2.54")
    ccy.value should be (2.54)
  }

  it should  "translate a price with more than two decimal places (10.54321) into the corresponding value with the same decimal places (10.54321)" in {
    val ccy = new Price("£10.54321")
    ccy.value should be (10.54321)
  }

  it should  "throw an exception for a malformed price" in {
    an [Exception] should be thrownBy new Price("not a price")
  }

}
