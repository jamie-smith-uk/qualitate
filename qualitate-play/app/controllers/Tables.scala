package uk.co.qualitate

import slick.driver.PostgresDriver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}
import scala.concurrent.Future


case class NativeAdvert(id: Long, title: String, clicks: Int, cost: Double, ccy: String, url: String, impressions: Int)

class NativeAdverts(tag: Tag)
  extends Table[NativeAdvert](tag, "NATIVEADVERTS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def title= column[String]("NAME")
  def clicks = column[Int]("CLICKS")
  def cost = column[Double]("COST")
  def ccy = column[String]("CCY")
  def url = column[String]("URL")
  def impressions = column[Int]("IMPRESSIONS")
  def * = (id, title, clicks, cost, ccy, url, impressions) <> (NativeAdvert.tupled, NativeAdvert.unapply)

}

object NativeAdvertsDAO extends TableQuery(new NativeAdverts(_))

