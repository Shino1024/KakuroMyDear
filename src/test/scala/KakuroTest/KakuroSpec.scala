package KakuroTest

import Controllers.KakuroController
import Models.{BoardSize, KakuroCell}
import Util.Settings
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{GivenWhenThen, Matchers, PropSpec}

class KakuroSpec extends PropSpec with TableDrivenPropertyChecks with GivenWhenThen with Matchers {

  val examples =
    Table(
      ("size", "value"),
      (BoardSize.SMALL, 8),
        (BoardSize.MEDIUM, 10),
        (BoardSize.BIG, 12)
    )


  property("Kakuro Controller is able to generate three sizes of board") {

    forAll(examples) { (size, value) =>

      info("—————-")

      Given("we have chosen size: " + size)
        Settings.boardSize = size

      When("we start game and create new and fresh controller")
        val controller = new KakuroController
        controller.generateCellBoard()


      Then("the board should be a square with side length of " + value)
        assert(Settings.boardSize.id == value )
        assert(Settings.boardSize == size)

      And("the logic board representing black and white cells should be filled only with 0 or 1")
      for(i <- 0 until Settings.boardSize.id) {
        for (j <- 0 until Settings.boardSize.id) {
          val logicBoard= controller.getLogicBoard
          assert(logicBoard(i)(j) == 0 || logicBoard(i)(j) == 1)
        }
      }

      And("the kakuro board should be completely filled with kakuro cells")
        for(i <- 0 until Settings.boardSize.id) {
          for (j <- 0 until Settings.boardSize.id) {
              assert(controller.getKakuroBoard.getMatrixCell(i,j).isInstanceOf[KakuroCell])
          }
        }
    }

  }


}
