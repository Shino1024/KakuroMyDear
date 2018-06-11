package Views

import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.Button
import javafx.scene.layout.{HBox, Priority}
import javafx.scene.text.Text

object ViewGenerator {
  def generateButton(text: String, eventHandler: EventHandler[ActionEvent]): HBox = {
    val buttonView = new Button

    buttonView.setText(text)
    buttonView.setId("Button")
    buttonView.setOnAction(eventHandler)

    val container = new HBox(buttonView)
    container.setId("ButtonContainer")
    HBox.setHgrow(buttonView, Priority.ALWAYS)

    container
  }

  def generateCaption(text: String, captionType: CaptionType): HBox = {
    val textView = new Text(text)
    textView.setId(captionType.id)

    val container = new HBox(textView)
    container.setId(captionType.containerId)

    container
  }
}
