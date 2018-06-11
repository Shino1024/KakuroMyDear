package Views

sealed trait KakuroButton {
  def name: String
}
case object NewBoard extends KakuroButton {
  def name: String = "NEW BOARD"
}
case object Check extends KakuroButton {
  def name: String = "CHECK"
}
case object BoardQuit extends KakuroButton {
  def name: String = "BACK"
}
