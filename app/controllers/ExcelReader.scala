package uk.co.qualitate

import java.io.{File, FileInputStream}
import org.apache.poi.ss.usermodel.{Cell, WorkbookFactory, DataFormatter, Row}

import scala.collection.mutable.ListBuffer

class ExcelReader(adapter:ExcelDataAdapter) {

  def readFile(file: File):List[NativeAdvert] = {

    val wb = WorkbookFactory.create(new FileInputStream(file))
    val sheet = wb.getSheetAt(0)
    val nativeAdverts = new ListBuffer[NativeAdvert]()

    // TODO: more elegant way to get through format of data in file
    val rowIterator = sheet.rowIterator()
    // skip header -
    rowIterator.next()
    while (rowIterator.hasNext) {
      nativeAdverts+=extractObjectFromRow(rowIterator.next).toNativeAdvert()
    }
    nativeAdverts.toList
  }

  def extractObjectFromRow(row:Row): NativeAdvertInfo = {
    val cellIterator = row.cellIterator()
    val nativeAdvert = new NativeAdvertInfo
    while (cellIterator.hasNext) {
      val cell = cellIterator.next()
      nativeAdvert.mapValue(adapter.findMapping(cell.getColumnIndex), getCellString(cell))
    }
    nativeAdvert
  }

  def getCellString(cell: Cell) = {
    cell.getCellType match {
      case Cell.CELL_TYPE_NUMERIC =>
        new DataFormatter().formatCellValue(cell)
      case Cell.CELL_TYPE_STRING =>
        cell.getStringCellValue
      case _ => " "
    }
  }
}
