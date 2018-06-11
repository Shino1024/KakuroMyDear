package Util

import Models.HighscoreDatabase
import javafx.application.Platform

object UtilFunctions {
  def exitGame(): Unit = {
    HighscoreDatabase.saveHighscores()
    Platform.exit()
    System.exit(0)
  }
}
