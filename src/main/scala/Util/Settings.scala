package Util

import Models.BoardSize
import Models.BoardSize.BoardSize

object Settings {
  var boardSize: BoardSize = BoardSize.SMALL

  val databaseSize = 5

  val menuHeight = 540
  val menuWidth = 360

  val highscoreFilename = "highscores.txt"
}
