package Views

sealed trait CaptionType {
  def id: String
  def containerId: String
}

case object TitleCaption extends CaptionType {
  def id: String = "TitleCaption"
  def containerId: String = "TitleCaptionContainer"
}

case object MinorCaption extends CaptionType {
  def id: String = "MinorCaption"
  def containerId: String = "MinorCaptionContainer"
}

case object TinyCaption extends CaptionType {
  def id: String = "TinyCaption"
  def containerId: String = "TinyCaptionContainer"
}

