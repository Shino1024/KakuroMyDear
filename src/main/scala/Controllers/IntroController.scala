package Controllers

import Apps.{HighscoreApp, KakuroApp}
import Views.{GameQuit, IntroView, Play, Scores}
import Util.UtilFunctions

import javafx.event.{ActionEvent, EventHandler}
import javafx.stage.Stage

class IntroController extends GenericController {
  private val introView = new IntroView
  private var primaryStage: Stage = _

  override def setStage(stage: Stage): Unit = {
    primaryStage = stage
  }

  override def showStage(): Unit = {
    primaryStage.setTitle("KAKURO MY DEAR!")
    primaryStage.setResizable(false)

    introView.injectActionButtonHandler(Play, playButtonEventHandler(primaryStage))
    introView.injectActionButtonHandler(Scores, highscoreButtonEventHandler(primaryStage))
    introView.injectActionButtonHandler(GameQuit, quitButtonEventHandler(primaryStage))

    val scene = introView.generateScene
    scene.getStylesheets.add("Views/styles/styles.css")

    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def playButtonEventHandler(stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        stage.close()
        val kakuroApp: KakuroApp = new KakuroApp
        kakuroApp.start(stage)
      }
    }

    handler
  }

  def highscoreButtonEventHandler(stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        stage.close()
        val highscoreApp: HighscoreApp = new HighscoreApp
        highscoreApp.start(stage)
      }
    }

    handler
  }

  def quitButtonEventHandler(stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        UtilFunctions.exitGame()
      }
    }

    handler
  }

}