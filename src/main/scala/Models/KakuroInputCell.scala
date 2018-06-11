package Models

import java.util.Collections

import javafx.collections.{FXCollections, ObservableList}
import Views._
import javafx.scene.Node
import javafx.scene.layout.{GridPane, HBox, StackPane}
import javafx.scene.text.Text

class KakuroInputCell(row_pos: Int, column_pos: Int) extends KakuroCell {

  private val numbers: Array[Boolean] = new Array[Boolean](10)
  val NUMS = 10

  //INITIAL VALUE - EMPTY CELL
  private var box: HBox = _
  private var cellRepresentation: StackPane = _
  private val row: Int = row_pos
  private val column: Int = column_pos
  private var inputCellsArity: InputCellsArity = EmptyCell
  private var horizontalAuxiliarySumCell: AuxiliarySumCell = _
  private var verticalAuxiliarySumCell: AuxiliarySumCell = _



  def setAuxiliarySumCell(sumCell : AuxiliarySumCell): Unit = {

    val direction : SumDirection = sumCell.getDirection

    direction match {

      case Horizontal =>
        horizontalAuxiliarySumCell = sumCell

      case Vertical =>
        verticalAuxiliarySumCell = sumCell
    }
  }

  def setCellRepresentation(stackPane: StackPane): Unit = {
    cellRepresentation = stackPane
  }

  def setBox(hbox: HBox): Unit = {
    box = hbox
  }

  def getBox: HBox = box

  def getRow: Int = row

  def getColumn: Int = column

  def getHorizontalSum: Int = {

    if(horizontalAuxiliarySumCell != null)
      horizontalAuxiliarySumCell.getValue()
    else{
      println("horizontal Sum doesn't exist!")
      0
    }
  }

  def getVerticalSum: Int = {

    if(horizontalAuxiliarySumCell != null)
      verticalAuxiliarySumCell.getValue()
    else{
      println("vertical Sum doesn't exist!")
      0
    }
  }

  def setNumber(number: Int): Unit = {
    if (number >= 0 && number < NUMS) {
      numbers(number) = true
    }
  }

  def unsetNumber(number: Int): Unit = {
    if (number >= 0 && number < NUMS) {
      numbers(number) = false
    }
  }

  def isSetNumber(number: Int): Boolean = numbers(number)

  def amountOfNumbers(): Int = {

    var amount: Int = 0

    for(i <- 1 until NUMS; if numbers(i)) {
      amount = amount + 1
    }
    amount
  }

  def configureRepresentation(): Unit = {

    amountOfNumbers() match {

      case 0 => inputCellsArity = EmptyCell
      case 1 => inputCellsArity = SingleDigit
      case n if n > 1 => inputCellsArity = MultiDigit
    }

    inputCellsArity match {

      case EmptyCell =>

        var node = cellRepresentation.getChildren.get(0)

        node match {

          case _: Text =>
          case _: GridPane =>

            val children: ObservableList[Node] = FXCollections.observableArrayList(cellRepresentation.getChildren)
            Collections.swap(children, 0, 1)
            cellRepresentation.getChildren.setAll(children)
        }

        node = cellRepresentation.getChildren.get(0)

        node match {

          case node: Text =>
            node.setText("")
        }

      case SingleDigit =>

        var node = cellRepresentation.getChildren.get(0)

        node match {

          case _: Text =>
          case node: GridPane =>

            for(i <- 1 until NUMS){
              val currentNode = node.getChildren.get(i - 1)

              currentNode match{
                case cell:Text => cell.setText("")
              }
            }

            val children: ObservableList[Node] = FXCollections.observableArrayList(cellRepresentation.getChildren)
            Collections.swap(children, 0, 1)
            cellRepresentation.getChildren.setAll(children)
        }

        node = cellRepresentation.getChildren.get(0)

            node match {

              case node: Text =>
                for (i <- 1 until NUMS; if numbers(i))
                  node.setText(i.toString)
            }

      case MultiDigit =>

        var node = cellRepresentation.getChildren.get(0)

        node match {

          case _: GridPane =>
          case node: Text =>

            node.setText("")

            val children: ObservableList[Node] = FXCollections.observableArrayList(cellRepresentation.getChildren)
            Collections.swap(children, 0, 1)
            cellRepresentation.getChildren.setAll(children)
        }

        node = cellRepresentation.getChildren.get(0)

        node match {
          case node: GridPane =>

            for (i <- 1 until NUMS) {
              val currentNode = node.getChildren.get(i - 1)

              currentNode match {

                case currentNode: Text =>
                  if (numbers(i)) {

                   currentNode.setText(i.toString)
                  }else{
                    currentNode.setText("")
                  }
              }
            }
        }
    }
  }
}