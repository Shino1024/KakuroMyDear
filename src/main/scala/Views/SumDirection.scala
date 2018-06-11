package Views


sealed trait SumDirection {
  def name: String
}
case object Vertical extends SumDirection {
  def name: String = "VERTICAL"
}
case object Horizontal extends SumDirection {
  def name: String = "HORIZONTAL"
}
