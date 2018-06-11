package Apps

import Controllers.IntroController
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage

class IntroApp extends Application {
  private val introController = new IntroController

  override def start(primaryStage: Stage): Unit = {
    primaryStage.getIcons.add(new Image("file:assets/icon.png"))
    introController.setStage(primaryStage)
    introController.showStage()
  }

}
