package Apps

import Controllers.KakuroController
import Util._
import Views.{Check, BoardQuit, KakuroView, NewBoard}
import javafx.application.Application
import javafx.stage.Stage

class KakuroApp extends Application {
  private val kakuroController = new KakuroController

  override def start(primaryStage: Stage) {
    kakuroController.setStage(primaryStage)
    kakuroController.showStage()
  }
}
