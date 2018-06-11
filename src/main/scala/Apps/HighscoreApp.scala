package Apps

import Controllers.HighscoreController
import Models.{BoardSize, HighscoreDatabase}
import Views.HighscoreView
import javafx.application.Application
import javafx.stage.Stage

class HighscoreApp extends Application {
  private val highscoreController = new HighscoreController

  override def start(primaryStage: Stage): Unit = {
    highscoreController.setStage(primaryStage)
    highscoreController.showStage()
  }
}
