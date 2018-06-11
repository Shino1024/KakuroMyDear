package Controllers

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}

import Apps.IntroApp
import Models._
import Util.Settings
import Views._
import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.Duration

import scala.util.Random

class KakuroController extends GenericController {
  private val kakuroView = new KakuroView
  private var primaryStage: Stage = _

  private val kakuroBoard: KakuroBoard = new KakuroBoard(Settings.boardSize)
  private var logicBoard = generateLogicBoard(Settings.boardSize.id)
  private var sumBoard = new SumBoard()

  private var selectedCell: HBox = _

  private var startTime: LocalDateTime = LocalDateTime.now()

  private var timeline: Timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        kakuroView.updateTimerView(getTime)
      }
    }))

  def getKakuroBoard: KakuroBoard = kakuroBoard
  def getLogicBoard: Array[Array[Int]] = logicBoard
  def getSumBoard: SumBoard = sumBoard

  override def setStage(stage: Stage): Unit = {
    primaryStage = stage
  }


  override def showStage(): Unit = {
    kakuroView.injectKakuroBoard(generateCellBoard())

    kakuroView.injectActionButtonHandler(BoardQuit, backButtonHandler(primaryStage))
    kakuroView.injectActionButtonHandler(Check, checkButtonEventHandler(primaryStage))
    kakuroView.injectActionButtonHandler(NewBoard, newBoardEventHandler(primaryStage))

    kakuroView.injectNumberButtonHandler(numberButtonHandler)

    kakuroView.injectKeyButtonHandler(selectedCellHandler)

    kakuroView.injectSaveHighscoreButtonHandler(saveHighscoreButtonHandler)
    kakuroView.injectConfirmButtonHandler(confirmHighscoreButtonHandler)

//    kakuroView.injectSumsUpdaterHandler()

//    kakuroView.inject

    val scene = kakuroView.generateScene
    val boardName = Settings.boardSize.toString.toLowerCase
    primaryStage.setTitle("Kakuro game: " + boardName + " board")

    runClock()

    scene.getStylesheets.add("Views/styles/styles.css")
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  // BUTTON HANDLING
  def numberButtonHandler(text: String): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        if (kakuroView.isInputEnabled) {

          changeSelectedCellText(text)
        }
      }
    }

    handler
  }

  def backButtonHandler(stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        stage.close()
        val introApp = new IntroApp
        introApp.start(stage)
      }
    }

    handler
  }

  def winProcedure(): Unit = {
    kakuroView.disableInput()
    kakuroView.setFinishTime(getTime)
    kakuroView.displayWinBox()
    timeline.stop()
  }

  def wrongCheckProcedure(): Unit = {
    kakuroView.disableInput()
    kakuroView.displayCheckWrongBox()
  }

  def checkButtonEventHandler(_stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        if (kakuroView.isInputEnabled) {
//          if (sumBoard.checkBoard()) {
//            winProcedure()
//          } else {
//            wrongCheckProcedure()
//
//           winProcedure()
             println(sumBoard.checkBoard())
        }
      }
    }

    handler
  }

  def newBoardEventHandler(stage: Stage): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        if (kakuroView.isInputEnabled) {
          startTimer()
          finishClock()
          runClock()
          kakuroView.injectKakuroBoard(generateCellBoard())
          kakuroView.updateKakuroBoardView()
        }
      }
    }

    handler
  }

  def finishClock(): Unit = {
    timeline.stop()
  }

  def runClock(): Unit = {
    timeline.setCycleCount(Animation.INDEFINITE)
    timeline.play()
  }

  def saveHighscoreButtonHandler(nick: TextField): EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      def handle(e: ActionEvent): Unit = {
        val newHighscore = Highscore(nick.getText, LocalDate.now, getTime)
        HighscoreDatabase.updateHighscores(newHighscore, Settings.boardSize)
        kakuroView.removeWinBox()

        kakuroView.enableInput()

        startTimer()
        runClock()

        logicBoard = generateLogicBoard(Settings.boardSize.id)
        kakuroView.injectKakuroBoard(generateCellBoard())
        kakuroView.updateKakuroBoardView()
      }
    }

    handler
  }

  def confirmHighscoreButtonHandler: EventHandler[ActionEvent] = {
    val handler = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        kakuroView.removeCheckWrongBox()
        kakuroView.enableInput()
      }
    }

    handler
  }

//  def injectSumsUpdaterHandler: EventHandler[ActionEvent] = {
//    val handler = new EventHandler[ActionEvent] {
//      override def handle(event: ActionEvent): Unit = {
//        val HSums = calculateHSums()
//        val VSums = calculateVSums()
//        kakuroView.updateSums(HSums, VSums)
//      }
//    }
//
//    handler
//  }
//
//  var globalList: List[List[Int]] = List()

//  private def getCombinations(currentSumList: List[Int], currentSum: Int, currentNumber: Int, numberOfNumbers: Int): List[Int] = {
//    if (currentSum < 0) {
//      return List()
//    }
//
//    if (currentSumList.length > numberOfNumbers) {
//      return List()
//    }
//
//    if (currentSum == 0) {
//      if (currentSumList.length == numberOfNumbers) {
//        return currentSumList ++ List(currentNumber)
//      } else {
//        return List()
//      }
//    }
//
////    var allSums = Seq()
//
//    for (i <- currentNumber to 9) {
//      globalList = globalList ++ getCombinations(currentSumList ++ List(currentNumber), currentSum - currentNumber, currentNumber + i, numberOfNumbers)
//    }
//  }

  private def calculateHSums(): List[List[Int]] = {
    val allSums = List()
    allSums
  }

  private def startTimer(): Unit = {
    startTime = LocalDateTime.now()
  }

  def getTime: LocalTime = {
    val endTime = LocalDateTime.now()

    val hoursDifference: Int = ChronoUnit.HOURS.between(startTime, endTime).asInstanceOf[Int]
    val minutesDifference: Int = (ChronoUnit.MINUTES.between(startTime, endTime) % 60).asInstanceOf[Int]
    val secondsDifference: Int = (ChronoUnit.SECONDS.between(startTime, endTime) % 60).asInstanceOf[Int]

    LocalTime.of(hoursDifference, minutesDifference, secondsDifference)
  }

  //CELL HANDLING
  def selectedCellHandler(cell: HBox): EventHandler[MouseEvent] = {
    val handler = new EventHandler[MouseEvent] {
      def handle(e: MouseEvent): Unit = {
        if (kakuroView.isInputEnabled) {
          changeCellSelection(cell)
        }
      }
    }

    handler
  }

  private def changeCellSelection(cell: HBox): Unit = {

      if(selectedCell == cell){

      unhighlightEveryCell()
      selectedCell.setId("InputCell")
      selectedCell = null

    }else {

// please delete this example box after editing

        for (i <- 0 until Settings.boardSize.id) {
          for (j <- 0 until Settings.boardSize.id) {

            val currentCell = kakuroBoard.getMatrixCell(i,j)

            currentCell match {

              case currentCell:KakuroInputCell =>
                if(currentCell.getBox == cell)
                println("Horizontal sum: " + currentCell.getHorizontalSum + ", Vertical sum: " + currentCell.getVerticalSum)

              case _ =>
            }

            }
          }
// end of example box

        if (selectedCell == null) {

          selectedCell = cell
          highlightAdjacentCells()
          cell.setId("SelectedInputCell")


        } else {
          unhighlightEveryCell()
          selectedCell.setId("InputCell")
          selectedCell = cell
          highlightAdjacentCells()
          cell.setId("SelectedInputCell")

        }
      }
  }

  def highlightAdjacentCells():Unit = {

    for (i <- 0 until Settings.boardSize.id) {
      for (j <- 0 until Settings.boardSize.id) {

        val element = kakuroBoard.getMatrixCell(i,j)

        element match {

          case cell:KakuroInputCell =>

            if(cell.getBox == selectedCell){

              val row = cell.getRow
              val column = cell.getColumn
              var markedFlag = true

              //UP
              for(k <- row to 0 by -1 ; if markedFlag){

               val cell = kakuroBoard.getMatrixCell(k,column)

                cell match {
                  case cell:KakuroInputCell => cell.getBox.setId("AdjacentCell")
                  case _ => markedFlag = false
                }
              }

              //DOWN
              markedFlag = true
              for(k <- row until Settings.boardSize.id; if markedFlag){

                val cell = kakuroBoard.getMatrixCell(k,column)

                cell match {
                  case cell:KakuroInputCell => cell.getBox.setId("AdjacentCell")
                  case _ => markedFlag = false
                }
              }

              //RIGHT
              markedFlag = true
              for(k <- column until Settings.boardSize.id; if markedFlag){

                val cell = kakuroBoard.getMatrixCell(row,k)

                cell match {
                  case cell:KakuroInputCell => cell.getBox.setId("AdjacentCell")
                  case _ => markedFlag = false
                }
              }

              //LEFT
              markedFlag = true
              for(k <- column to 0 by -1; if markedFlag){

                val cell = kakuroBoard.getMatrixCell(row,k)

                cell match {
                  case cell:KakuroInputCell => cell.getBox.setId("AdjacentCell")
                  case _ => markedFlag = false
                }
              }

            }

          case _ =>
        }
      }
    }
  }


  def unhighlightEveryCell(): Unit = {

    for (i <- 0 until Settings.boardSize.id) {
      for (j <- 0 until Settings.boardSize.id) {

        val element = kakuroBoard.getMatrixCell(i,j)

        element match {

          case cell:KakuroInputCell =>
            cell.getBox.setId("InputCell")
          case _ =>
        }
      }
    }
  }

  private def changeSelectedCellText(text: String): Unit = {

    if (selectedCell != null) {

      for(i <- 0 until Settings.boardSize.id){
        for(j <- 0 until Settings.boardSize.id){

          kakuroBoard.getMatrixCell(i, j) match{

            case cell:KakuroInputCell =>

              if(cell.getBox == selectedCell) {

                if (cell.isSetNumber(text.toInt)) {
                  cell.unsetNumber(text.toInt)
                  cell.configureRepresentation()
                } else {
                  cell.setNumber(text.toInt)
                  cell.configureRepresentation()
                }
              }

            case _ =>
          }
        }
      }

    }
  }

  //BOARD GENERATORS
  def generateCellBoard(): KakuroBoard = {
    logicBoard = generateLogicBoard(Settings.boardSize.id)

    //TEMPORARY ARRAY ONLY FOR CHECKING THE COVER OF LOGIC BOARD
    val markedBoard = Array.ofDim[Int](Settings.boardSize.id, Settings.boardSize.id)


    //WE NEED TO MARK THE BOARD IN ORDER TO MAKE SURE EVERY CELL I COVERED BY DEFINED SUM,
    // AND THERE ARE NO MULTIPLE SUMS FOR ROW OR COLUMN
    for (i <- 0 until Settings.boardSize.id) {
      for (j <- 0 until Settings.boardSize.id) {
        markedBoard(i)(j) = 0
      }
    }

    // FIRSTLY WE NEED ALL INPUT CELLS TO MATCH THEM TO THE SUM LIST
    // IN THE NEXT STEP
    for (i <- 0 until Settings.boardSize.id) {
      for (j <- 0 until Settings.boardSize.id) {
        logicBoard(i)(j) match {
          case 0 =>
          case 1 => kakuroBoard.setMatrixCell(i, j, new KakuroInputCell(i, j))

        }
      }
    }

    //GENERATE PROPER AMOUNT OF HINT CELLS, GENERATE SUM CHECKING
    for (i <- 0 until Settings.boardSize.id) {
      for (j <- 0 until Settings.boardSize.id) {
        logicBoard(i)(j) match {
          case 0 =>
            val kakuroCell = new KakuroSumCell(0, 0)

            var markedFlag = true

            //DOWN DIRECTION
            if (i + 1 < Settings.boardSize.id && logicBoard(i + 1)(j) != 0 ) {


              var inputCellsNumber = 0

              markedFlag = true
              for (k <- i until Settings.boardSize.id; if markedFlag) {
                if (logicBoard(k)(j) == 0 && k != i) {
                  markedFlag = false
                } else {
                  if(k != i)
                  inputCellsNumber = inputCellsNumber + 1
                }
              }

                sumBoard.createNewList(Vertical)

                markedFlag = true
                for (k <- i until Settings.boardSize.id; if markedFlag) {
                  if (logicBoard(k)(j) == 0 && k != i) {
                    markedFlag = false
                  } else {
                    markedBoard(k)(j) = 1

                    if (k != i)
                      sumBoard.addMatrixInputCell(kakuroBoard.getMatrixCell(k, j))

                  }
                }

                // WE HAVE TO COMPUTE MAX AND MIN SUM POSSIBLE TO MATCH
                // OF COURSE THESE ARE ARITHMETIC SERIES FOR FIRST N AND LAST N NUMBERS

                val minSum = ((1 + inputCellsNumber) * inputCellsNumber) / 2
                val maxSum = ((9 - inputCellsNumber + 1 + 9) * inputCellsNumber) / 2

                // NOW LET'S CHOSE ANY NUMBER BETWEEN ABOVE SUMS
                var sumValue = minSum

                if(maxSum != minSum){
                  sumValue = Random.nextInt(maxSum - minSum) + minSum
                }

                kakuroCell.setDownValue(sumValue)
                sumBoard.addMatrixSumList(sumValue)

            }

            //RIGHT DIRECTION
            if (j + 1 < Settings.boardSize.id && logicBoard(i)(j + 1) != 0) {


              var inputCellsNumber = 0

              markedFlag = true
              for (k <- j until Settings.boardSize.id; if markedFlag) {
                if (logicBoard(i)(k) == 0 && k != j)
                {
                  markedFlag = false
                } else {
                  if(k != j)
                  inputCellsNumber = inputCellsNumber + 1
                }
              }

                sumBoard.createNewList(Horizontal)

                markedFlag = true
                for (k <- j until Settings.boardSize.id; if markedFlag) {
                  if (logicBoard(i)(k) == 0 && k != j) {
                    markedFlag = false
                  } else {
                    markedBoard(i)(k) = 1

                    if (k != j)
                      sumBoard.addMatrixInputCell(kakuroBoard.getMatrixCell(i, k))

                  }
                }

                val minSum = ((1 + inputCellsNumber) * inputCellsNumber) / 2
                val maxSum = ((9 - inputCellsNumber + 1 + 9) * inputCellsNumber) / 2

                var sumValue = minSum

                if(maxSum != minSum){
                   sumValue = Random.nextInt(maxSum - minSum) + minSum
                }

                kakuroCell.setRightValue(sumValue)
                sumBoard.addMatrixSumList(sumValue)

            }

            //WHEN BLACK CELL DON'T NEED TO HAVE NUMBER, BUT WE HAVE TO MARKED IT AS COVERED
            markedBoard(i)(j) = 1
            kakuroBoard.setMatrixCell(i, j, kakuroCell)

          case _ =>
        }
      }
    }
    
    //WE HAVE TO CHECK WHETER BOARD IS FULLY MARKED OR NOT
    if(checkMarkedBoard(markedBoard)) {
      kakuroBoard
    } else {
      sumBoard = new SumBoard
      generateCellBoard()
    }
  }

  def generateLogicBoard(boardSize: Int): Array[Array[Int]] = {
    val board = Array.ofDim[Int](boardSize, boardSize)
    // NOT DECIDED CELL -> -1
    //BLACK 0
    //WHITE 1
    for (i <- 0 until boardSize) {
      for (j <- 0 until boardSize) {
        board(i)(j) = -1
      }
    }

    //1. BORDERS
    //FIRST ROW AND FIRST COLUMN
    for (i <- 0 until boardSize) {
      board(0)(i) = 0 // range of {0,1}
      board(i)(0) = 0

    }

    //LAST ROW AND LAST COLUMN
    for (i <- 0 until boardSize) {
      board(boardSize - 1)(i) = (Random.nextFloat() + 0.9).toInt
      board(i)(boardSize - 1) = (Random.nextFloat() + 0.9).toInt
      // 2. COLUMN 2 AND N-1

      if (board(i)(0) == 1 && i != boardSize - 1 && i != 0) {
        board(i)(boardSize - 2) = 1
        board(boardSize - 2)(i) = 1
      }
    }

    //3 CENTER
    for (i <- 1 until boardSize / 2) {
      if (board(boardSize / 2)(i - 1) == 1) {
        if (i == 1) {
          board(boardSize / 2)(i) = 1
          board(boardSize / 2)(boardSize - i - 1) = 1

        } else {
          board(boardSize / 2)(i) = 0
          board(boardSize / 2)(boardSize - i - 1) = 0
        }
      } else {
        board(boardSize / 2)(i) = 1
        board(boardSize / 2)(boardSize - i - 1) = 1
      }
    }

    if (boardSize % 2 == 1) board(boardSize / 2)(boardSize / 2) = 1

    //4. RANDOMIZE THE REST
    for (i <- 1 until (boardSize / 2)) {
      for (j <- 1 until boardSize) {
        if (board(i)(j) == -1) {
          board(i)(j) = (Random.nextFloat() + 0.9).toInt
          board(boardSize - i - 1)(boardSize - j - 1) = board(i)(j)
        }
      }
    }

    // 5. RELEASE BOUNDED WHITE  OR BLACK CELLS
    for (i <- 1 to (boardSize / 2)) {
      for (j <- 1 until (boardSize - 1)) {
        if (board(i - 1)(j) == 0 &&
          board(i)(j - 1) == 0 &&
          board(i + 1)(j) == 0 &&
          board(i)(j + 1) == 0 &&
          board(i)(j) == 1) {

          board(i - 1)(j) = 1
          board(i)(j - 1) = 1
          board(i + 1)(j) = 1
          board(i)(j + 1) = 1

          board(boardSize - i - 2)(boardSize - j - 1) = 1
          board(boardSize - i - 1)(boardSize - j - 2) = 1
          board(boardSize - i)(boardSize - j - 1) = 1
          board(boardSize - i - 1)(boardSize - j) = 1
        }
      }
    }

    //CORNERS
    board(0)(boardSize - 1) = 0
    board(0)(0) = 0
    board(boardSize - 1)(boardSize - 1) = 0
    board(boardSize - 1)(0) = 0


    // LAST STEP -> ELIMINATE SINGLE WHITE CELLS AND TOO LONG SETS OF WHITE CELLS
    if(checkLogicBoard(board)){
      board
    }else{
      generateLogicBoard(Settings.boardSize.id)
    }

  }

  def checkLogicBoard(board: Array[Array[Int]]): Boolean = {

    var noWhiteCellsFlag = false
    var tooMuchWhiteCells = true

    while(!noWhiteCellsFlag || tooMuchWhiteCells) {
      noWhiteCellsFlag = true
      tooMuchWhiteCells = false

      for (i <- 0 until Settings.boardSize.id) {
        for (j <- 0 until Settings.boardSize.id) {

          board(i)(j) match {
            case 0 =>

              // DOWN DIRECTION
              if (i + 1 < Settings.boardSize.id && board(i + 1)(j) != 0) {

                var inputCellsNumber = 0

                var markedFlag = true
                for (k <- i until Settings.boardSize.id; if markedFlag) {
                  if (board(k)(j) == 0 && k != i) {
                    markedFlag = false
                  } else {
                    if (k != i)
                      inputCellsNumber = inputCellsNumber + 1
                  }
                }

                if (inputCellsNumber == 1) {
                  noWhiteCellsFlag = false
                  board(i + 1)(j) = 0

                }
                if (inputCellsNumber > 9){
                  return false
                }
              }

              //RIGHT DIRECTION
              if (j + 1 < Settings.boardSize.id && board(i)(j + 1) != 0) {

                var inputCellsNumber = 0

                var markedFlag = true
                for (k <- j until Settings.boardSize.id; if markedFlag) {
                  if (board(i)(k) == 0 && k != j) {
                    markedFlag = false
                  } else {
                    if (k != j)
                      inputCellsNumber = inputCellsNumber + 1
                  }
                }

                if (inputCellsNumber == 1) {
                  noWhiteCellsFlag = false
                  board(i)(j + 1) = 0

                }
                if (inputCellsNumber > 9){
                  return false
                }
              }

            case _ =>
          }
        }
      }
    }

    true
  }

  def checkMarkedBoard(markedBoard: Array[Array[Int]]): Boolean = {

    for (i <- 0 until Settings.boardSize.id) {
      for (j <- 0 until Settings.boardSize.id) {
        if (markedBoard(i)(j) == 0)
            return false
      }
    }
    true
  }

}