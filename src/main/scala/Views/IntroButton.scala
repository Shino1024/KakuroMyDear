package Views

sealed trait IntroButton {
  def name: String
}
case object Play extends IntroButton {
  def name: String = "PLAY"
}
case object Scores extends IntroButton {
  def name: String = "SCORES"
}
case object GameQuit extends IntroButton {
  def name: String = "QUIT"
}