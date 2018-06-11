package Controllers

import Apps.IntroApp
import Models.HighscoreDatabase
import Views.HighscoreView
import javafx.event.{ActionEvent, EventHandler}
import javafx.stage.Stage

class HighscoreController extends GenericController {
  private val highscoreView = new HighscoreView
  private var primaryStage: Stage = _

  override def setStage(stage: Stage): Unit = {
    primaryStage = stage
  }

  override def showStage(): Unit = {
    primaryStage.setTitle("Kakuro highscores")
    primaryStage.setResizable(false)

    HighscoreDatabase.parseHighscores()

    highscoreView.injectBackButtonHandler(backButtonEventHandler(primaryStage))

    val scene = highscoreView.generateScene
    scene.getStylesheets.add("Views/styles/styles.css")

    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def backButtonEventHandler(stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        stage.close()
        val introApp: IntroApp = new IntroApp
        introApp.start(stage)
      }
    }

    handler
  }

}
