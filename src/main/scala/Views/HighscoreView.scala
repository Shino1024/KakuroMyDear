package Views

import java.time.{LocalDate, LocalTime}

import Models.BoardSize.BoardSize
import Models.{BoardSize, Highscore, HighscoreDatabase}
import Views.ViewGenerator.{generateButton, generateCaption}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{TableColumn, TableView}
import javafx.scene.layout.GridPane

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class HighscoreView extends GenericView {
  var highscoreTables: List[TableView[Highscore]] = List()

  val highscoreColumnTitles: List[String] = List(
    "Name",
    "Date",
    "Time"
  )

  private var backButtonHandler: EventHandler[ActionEvent] = _

  def injectBackButtonHandler(buttonEventHandler: EventHandler[ActionEvent]): Unit = {
    backButtonHandler = buttonEventHandler
  }

  private def getHighscores(boardSize: BoardSize): List[Highscore] = {
    HighscoreDatabase.fetchHighscores(boardSize)
  }

  private def generateTable(boardSize: BoardSize): TableView[Highscore] = {
    var columns = new ListBuffer[TableColumn[Highscore, String]]()
    for (title <- highscoreColumnTitles) {
      val column = new TableColumn[Highscore, String](title)
      column.setCellValueFactory(new PropertyValueFactory(title.toLowerCase))
      columns += column
    }

    val highscoreTable = new TableView[Highscore]()
    highscoreTable.setId("HighscoreTable")
    val columnWidths = List(0.6, 0.18, 0.18)
    for ((column, columnWidth) <- columns zip columnWidths) {
      column.prefWidthProperty().bind(highscoreTable.widthProperty().multiply(columnWidth))
      column.setResizable(false)
      highscoreTable.getColumns.add(column)
    }

    val highscoreList = getHighscores(boardSize).asJava
    val observableHighscoresList = FXCollections.observableArrayList(highscoreList)
    highscoreTable.setItems(observableHighscoresList)

    highscoreTable
  }

  def generateTables(): GridPane = {
    val tablesPane = new GridPane()

    tablesPane.add(generateCaption("Highscores", TitleCaption), 0, 0)

    for ((boardSize, i) <- BoardSize.values.zipWithIndex) {
      val sectionName = boardSize.toString.capitalize + ":"
      tablesPane.add(generateCaption(sectionName, MinorCaption), 0, 2 * i + 1)
      tablesPane.add(generateTable(boardSize), 0, 2 * (i + 1))
    }

    tablesPane.add(generateButton("Back", backButtonHandler), 0, BoardSize.values.toArray.length * 2 + 2)

    tablesPane
  }

  override def generateScene: Scene = {
    val root = generateTables()
    root.setId("App")
    val scene = new Scene(root)

    scene
  }
}
