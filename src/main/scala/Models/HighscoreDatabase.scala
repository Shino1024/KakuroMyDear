package Models

import java.io._

import Models.BoardSize.BoardSize
import Util.Settings

import scala.collection.{mutable => m}

object HighscoreDatabase {
  private var highscoreMap: m.HashMap[BoardSize, List[Highscore]] = new m.HashMap[BoardSize, List[Highscore]]()

  def fetchHighscores(boardSize: BoardSize): List[Highscore] = {
    highscoreMap.get(boardSize) match {
      case Some(highscores) => highscores
      case None => List()
    }
  }

  def updateHighscores(newHighscore: Highscore, boardSize: BoardSize): Unit = {
    highscoreMap.get(boardSize) match {
      case Some(highscores) =>
        val betterHighscores = highscores.takeWhile(highscore => highscore.time.isBefore(newHighscore.time))
        val sameHighscores = highscores.filter(highscore => highscore.time == newHighscore.time)
        val worseHighscores = highscores.reverse.takeWhile(highscore => highscore.time.isAfter(newHighscore.time))
        val newHighscores = betterHighscores ++ sameHighscores ++ List(newHighscore) ++ worseHighscores.reverse
        highscoreMap.update(boardSize, newHighscores.take(5))

      case None =>
        highscoreMap.update(boardSize, List(newHighscore))
    }
  }

  def parseHighscores(): Unit = {
    val in = new ObjectInputStream(new FileInputStream(Settings.highscoreFilename))
    highscoreMap = in.readObject().asInstanceOf[m.HashMap[BoardSize, List[Highscore]]]

    in.close()
  }

  def saveHighscores(): Unit = {
    val out = new ObjectOutputStream(new FileOutputStream(Settings.highscoreFilename))
    out.writeObject(highscoreMap)

    out.close()
  }
}
