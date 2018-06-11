package Models

import Views.SumDirection

class AuxiliarySumCell(val input_sumDirection: SumDirection) {

  private var value: Int = _
  private var inputCellList: List[KakuroInputCell] = _

  private val sumDirection:SumDirection = input_sumDirection



  def setValue(input_value: Int):Unit = {
    value = input_value
  }

  def setInputCellList(input_list: List[KakuroInputCell]):Unit ={
    inputCellList = input_list
  }


  def getDirection(): SumDirection = sumDirection
  def getValue():Int = value
  def getInputCellList(): List[KakuroInputCell] = inputCellList

}
