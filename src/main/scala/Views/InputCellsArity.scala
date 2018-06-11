package Views

sealed trait  InputCellsArity {

  def mode: String

}

case object EmptyCell extends InputCellsArity{

  def mode: String = "EmptyCell"
}

case object SingleDigit extends InputCellsArity{

  def mode: String = "SingleDigit"
}

case object MultiDigit extends InputCellsArity{

  def mode: String = "MultiDigit"
}