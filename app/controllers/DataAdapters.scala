package uk.co.qualitate

import scala.collection.mutable._

class DataAdapters {}

abstract class AdapterLogic(){}

class ExcelAdapterLogic(classification:NativeAdvertClassification.Data, index: Int) extends AdapterLogic{
  var _index =index
  var _classification = classification
}

abstract class DataAdapter {}

abstract class ApiDataAdapter extends  DataAdapter{}

abstract class ExcelDataAdapter extends DataAdapter{

  val mappings = new ArrayBuffer[ExcelAdapterLogic]

  def mapping(): Array[ExcelAdapterLogic] ={
    mappings.toArray
  }

  def addCase(logicCase: ExcelAdapterLogic) ={
    mappings+=logicCase
  }

  def findMapping( mappingIndex:Int ):NativeAdvertClassification.Data={
    val list = mappings.filter(x=> x._index == mappingIndex)
    if (list.isEmpty) return NativeAdvertClassification.NONE
    list.head._classification
  }
}

object OutbrainDataAdapter extends ExcelDataAdapter{
  //TODO: read mapping from a file
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.TITLE, 0)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.CLICKS, 1)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.COST, 2)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.IMPRESSIONS, 3)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.URL, 6)
}


object TaboolaDataAdapter extends ExcelDataAdapter{
  //TODO: read mapping from a file
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.TITLE, 1)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.CLICKS, 4)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.COST, 7)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.IMPRESSIONS, 2)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.URL, 0)
}

object ContentClickDataAdapter extends ExcelDataAdapter{
  //TODO: read mapping from a file
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.TITLE, 1)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.CLICKS, 5)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.COST, 8)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.IMPRESSIONS, 4)
  mappings+= new ExcelAdapterLogic(NativeAdvertClassification.URL, 3)
}

