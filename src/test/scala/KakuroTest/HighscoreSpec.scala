package KakuroTest

import java.io.File
import java.time.{LocalDate, LocalTime}

import Models.{BoardSize, Highscore, HighscoreDatabase}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{BeforeAndAfter, GivenWhenThen, Matchers, PropSpec}

class HighscoreSpec extends PropSpec with GivenWhenThen with Matchers with BeforeAndAfter with TableDrivenPropertyChecks {
  val examples = List(
    (BoardSize.SMALL, Highscore("Jarek1", LocalDate.now(), LocalTime.of(0, 3, 4))),
    (BoardSize.SMALL, Highscore("Jarek2", LocalDate.now(), LocalTime.of(0, 2, 8))),
    (BoardSize.SMALL, Highscore("Jarek3", LocalDate.now(), LocalTime.of(0, 5, 32))),
    (BoardSize.SMALL, Highscore("Jarek4", LocalDate.now(), LocalTime.of(0, 7, 8))),
    (BoardSize.SMALL, Highscore("Jarek5", LocalDate.now(), LocalTime.of(0, 5, 0))),
    (BoardSize.SMALL, Highscore("Mateusz1", LocalDate.now(), LocalTime.of(0, 3, 4))),
    (BoardSize.BIG, Highscore("Mateusz2", LocalDate.now(), LocalTime.of(0, 3, 4))),
    (BoardSize.BIG, Highscore("Mateusz3", LocalDate.now(), LocalTime.of(0, 3, 5))),
    (BoardSize.BIG, Highscore("Mateusz4", LocalDate.now(), LocalTime.of(1, 3, 4))),
    (BoardSize.BIG, Highscore("Mateusz5", LocalDate.now(), LocalTime.of(0, 6, 4))),
  )

  val sizes = Table(
    "size",
    BoardSize.SMALL,
    BoardSize.MEDIUM,
    BoardSize.BIG
  )

  before {
    val highscoresFile = new File("highscores.txt")
    val highscoresCopyFile = new File("highscores_copy.txt")
    highscoresFile.renameTo(highscoresCopyFile)
  }

  property("Highscores should be serialized and deserialized to the same state") {
    for ((size, highscore) <- examples) {
      HighscoreDatabase.updateHighscores(highscore, size)
    }

    val smallSizeHighscores = HighscoreDatabase.fetchHighscores(BoardSize.SMALL)
    val mediumSizeHighscores = HighscoreDatabase.fetchHighscores(BoardSize.MEDIUM)
    val bigSizeHighscores = HighscoreDatabase.fetchHighscores(BoardSize.BIG)

    HighscoreDatabase.saveHighscores()

    HighscoreDatabase.parseHighscores()

    assert(smallSizeHighscores == HighscoreDatabase.fetchHighscores(BoardSize.SMALL))
    assert(mediumSizeHighscores == HighscoreDatabase.fetchHighscores(BoardSize.MEDIUM))
    assert(bigSizeHighscores == HighscoreDatabase.fetchHighscores(BoardSize.BIG))
  }

  property("There should be only 5 highscores for each board size") {
    Given("a set of highscores")
    val testHighscores = List(
      Highscore("Keraj1", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj2", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj3", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj4", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj5", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj6", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj7", LocalDate.now(), LocalTime.of(0, 1, 2)),
      Highscore("Keraj8", LocalDate.now(), LocalTime.of(0, 1, 2))
    )

    When("the highscores are saved to the database")
    for (highscore <- testHighscores) {
      HighscoreDatabase.updateHighscores(highscore, BoardSize.MEDIUM)
    }

    Then("only 5 highscores should remain")
    assert(HighscoreDatabase.fetchHighscores(BoardSize.MEDIUM).length == 5)
  }

  property("Highscores should be sorted") {
    forAll(sizes) { size =>
      var highscoresSorted = true
      val highscores = HighscoreDatabase.fetchHighscores(size)
      for (i <- 0 until highscores.length - 1) {
        if (highscores(i).time.isAfter(highscores(i + 1).time)) {
          highscoresSorted = false
        }
      }
      assert(highscoresSorted)
    }
  }

  after {
    val highscoresFile = new File("highscores.txt")
    val highscoresCopyFile = new File("highscores_copy.txt")
    highscoresCopyFile.renameTo(highscoresFile)
    val removeFile = new File("highscores_copy.txt")
    removeFile.delete()
  }

}
