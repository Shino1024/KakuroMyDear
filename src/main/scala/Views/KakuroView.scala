package Views

import java.time.LocalTime

import ViewGenerator.generateButton
import Models._
import Util._
import ViewGenerator._
import javafx.scene.control.TextField
import javafx.scene.layout.{GridPane, HBox, Priority, StackPane}
import javafx.scene.text.Text
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.input.MouseEvent

import scala.collection.mutable

class KakuroView extends GenericView {
  private val actionButtonHandlers = new mutable.HashMap[KakuroButton, EventHandler[ActionEvent]]()
  private var keyButtonHandler: HBox => EventHandler[MouseEvent] = _
  private var numberButtonHandler: String => EventHandler[ActionEvent] = _

  private var saveHighscoreButtonHandler: TextField => EventHandler[ActionEvent] = _
  private var confirmButtonHandler: EventHandler[ActionEvent] = _

  private var kakuroBoard: KakuroBoard = _

  private var finishTime: LocalTime = _

  private var inputDisabled = false

  private var timerView: HBox = _

  private val root: StackPane = new StackPane

  private val rowSpan = Settings.boardSize.id + 5

  private def dummyHandler[T <: javafx.event.Event]() = new EventHandler[T] {
    override def handle(event: T): Unit = {}
  }

  def isInputEnabled: Boolean = !inputDisabled

  def injectActionButtonHandler(kakuroButton: KakuroButton, buttonEventHandler: EventHandler[ActionEvent]): Unit = {
    actionButtonHandlers.update(kakuroButton, buttonEventHandler)
  }

  def injectKeyButtonHandler(buttonEventHandler: HBox => EventHandler[MouseEvent]): Unit = {
    keyButtonHandler = buttonEventHandler
  }

  def injectNumberButtonHandler(buttonEventHandler: String => EventHandler[ActionEvent]): Unit = {
    numberButtonHandler = buttonEventHandler
  }

  def injectSaveHighscoreButtonHandler(buttonEventHandler: TextField => EventHandler[ActionEvent]): Unit = {
    saveHighscoreButtonHandler = buttonEventHandler
  }

  def injectConfirmButtonHandler(buttonEventHandler: EventHandler[ActionEvent]): Unit = {
    confirmButtonHandler = buttonEventHandler
  }

  def setFinishTime(time: LocalTime): Unit = {
    finishTime = time
  }

  def disableInput(): Unit = {
    inputDisabled = true
  }

  def enableInput(): Unit = {
    inputDisabled = false
  }

  def injectKakuroBoard(_kakuroBoard: KakuroBoard): Unit = {
    kakuroBoard = _kakuroBoard
  }

  def controlHandle[T <: javafx.event.Event](handler: EventHandler[T]): EventHandler[T] = {
    if (inputDisabled) {
      dummyHandler[T]()
    } else {
      handler
    }
  }

  def generateActionButton(kakuroButton: KakuroButton): HBox = {
    actionButtonHandlers.get(kakuroButton) match {
      case Some(handler) => ViewGenerator.generateButton(kakuroButton.name, handler)
      case None => throw new NoSuchElementException("The " + kakuroButton.name.toLowerCase + " handler hasn't been installed.")
    }
  }

  private def createEmptyContainer(): HBox = {
    val emptyContainer = new HBox
    emptyContainer.setId("EmptyCell")

    emptyContainer
  }

  def createInfoContainer(info: String): HBox = {
    val text: Text = new Text
    text.setText(info)
    text.setId("InfoText")

    val container = new HBox(text)
    HBox.setHgrow(text, Priority.ALWAYS)
    container.setId("EmptyCell")

    container
  }

  def createContainer(kakuroCell: KakuroCell): HBox = {
    kakuroCell match {
      case kakuroCell: KakuroSumCell =>
        val text = new Text
        text.setId("HintCellText")

        (kakuroCell.getDownValue, kakuroCell.getRightValue) match {
            case (0, 0) =>
              text.setText("")

            case (0, value) if value != 0 =>
              text.setText("     " + value.toString + "\n")

            case (value, 0) if value != 0 =>
              text.setText("\n" + value.toString + "    ")

            case (value1, value2) =>
              text.setText("    " + value2.toString + "\n" + value1.toString + "    ")
        }

        val container = new HBox(text)
        container.setId("HintCell")

        container

      case kakuroCell: KakuroInputCell =>

        val cellRepresentation = new StackPane

        val singleCell = new Text
        singleCell.setText("")
        singleCell.setId("InputCellText")

        val multiCell = new GridPane

        for(i <- 0 to 2){
          for(j <- 0 to 2){
            val currentText = new Text
            currentText.setText("")
            currentText.setId("InputMultiCellText")
            currentText.setWrappingWidth(13)
            multiCell.add(currentText, j, i)
          }
        }

        cellRepresentation.getChildren.addAll(singleCell, multiCell)

        val container = new HBox(cellRepresentation)
        container.setId("InputCell")
        HBox.setHgrow(cellRepresentation, Priority.ALWAYS)
        container.setOnMouseClicked(controlHandle[MouseEvent](keyButtonHandler(container)))

        kakuroCell.setBox(container)
        kakuroCell.setCellRepresentation(cellRepresentation)
        container

      case _: KakuroEmptyCell =>
        val textField = new TextField()

        val container = new HBox(textField)
        container.setId("EmptyCell")
        HBox.setHgrow(textField, Priority.ALWAYS)

        container
    }
  }

  def updateKakuroBoardView(): Unit = {
    val newBoardView = new GridPane
    newBoardView.add(timerView, 0, 0, rowSpan, 1)
    newBoardView.add(makeKakuroBoard(), 0, 1, rowSpan,1)
    newBoardView.add(makeControlPanel(), 0, 2, rowSpan, 1)
    root.getChildren.set(0, newBoardView)
  }

  def makeKakuroBoard(): GridPane = {
    val root = new GridPane
    root.setId("KakuroControlPanel")

    println(kakuroBoard)
    for (i <- 0 until kakuroBoard.size.id) {
      for (j <- 0 until kakuroBoard.size.id) {
        root.add(createContainer(kakuroBoard.getMatrixCell(i,j)), j, i)
      }
    }

    root
  }

  def makeControlPanel(): GridPane = {
    val root = new GridPane
    root.setId("KakuroControlPanel")

    for (i <- 0 to 2) {
      for (j <- 0 to 2) {
        val strNum = (i * 3 + j + 1).toString
        val button = generateButton(strNum, controlHandle[ActionEvent](numberButtonHandler(strNum)))
        button.setMaxWidth(20f)
        root.add(button, j, i)
      }
    }

    for (i <- 0 to 2) {
      val separator = createEmptyContainer()
      separator.setMinWidth(5f * Settings.boardSize.id)
      root.add(separator, 3, i)
    }

    root.add(generateActionButton(Check), 9, 0, 1, 1)
    root.add(generateActionButton(NewBoard), 9, 1, 1, 1)
    root.add(generateActionButton(BoardQuit), 9, 2, 1, 1)

    root
  }

  def generateWinBox(): GridPane = {
    val winPane = new GridPane()
    winPane.setId("Window")

    val winCaption = generateCaption("Congratulations!", MinorCaption)
    val timeCaption = generateCaption("Your time: ", TinyCaption)
    val timeValue = generateCaption(finishTime.toString, TinyCaption)

    val nick = new TextField
    nick.setPromptText("Your nick")

    val submitButton = generateButton("Submit", saveHighscoreButtonHandler(nick))

    winPane.add(winCaption, 0, 0, 2, 1)
    winPane.add(timeCaption, 0, 1)
    winPane.add(timeValue, 1, 1)
    winPane.add(nick, 0, 2)
    winPane.add(submitButton, 1, 2)

    winPane
  }

  def generateCheckWrongBox(): GridPane = {
    val cwPane = new GridPane()
    cwPane.setId("Window")

    val informationButton = generateCaption("Not yet there,\nbut don't give up!", MinorCaption)
    val confirmButton = generateButton("OK", confirmButtonHandler)

    cwPane.add(informationButton, 0, 0)
    cwPane.add(confirmButton, 0, 1)

    cwPane
  }

  def displayWinBox(): Unit = {
    root.getChildren.add(generateWinBox())
  }

  def displayCheckWrongBox(): Unit = {
    root.getChildren.add(generateCheckWrongBox())
  }

  def removeWinBox(): Unit = {
    root.getChildren.remove(root.getChildren.size - 1)
  }

  def removeCheckWrongBox(): Unit = {
    root.getChildren.remove(root.getChildren.size - 1)
  }

  def updateTimerView(time: LocalTime): Unit = {
    timerView.getChildren.get(0).asInstanceOf[Text].setText("Time: " + time.toString)
    println(time.toString)
  }

  override def generateScene: Scene = {
    val gameScene = new GridPane
    val kakuroBoardView = makeKakuroBoard()
    val controlPanelView = makeControlPanel()
    controlPanelView.setId("KakuroControlPanel")

    timerView = generateCaption("", MinorCaption)

    gameScene.add(timerView, 0, 0, rowSpan, 1)
    gameScene.add(kakuroBoardView, 0, 1, rowSpan,1)
    gameScene.add(controlPanelView, 0, 2, rowSpan, 1)
    root.getChildren.add(gameScene)
    println("Centered????\n\n\n")

    root.setId("App")
    val scene = new Scene(root)

    scene
  }

}