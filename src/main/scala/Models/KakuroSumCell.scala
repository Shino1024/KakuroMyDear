package Models


class KakuroSumCell(val _rightValue: Int, val _downValue: Int) extends KakuroCell {

  var rightValue: Int = _rightValue
  var downValue: Int = _downValue

  def getRightValue: Int = rightValue
  def getDownValue: Int = downValue

  def setRightValue(value:Int):Unit = {
    rightValue = value
  }

  def setDownValue(value:Int):Unit = {
    downValue = value
  }


}
