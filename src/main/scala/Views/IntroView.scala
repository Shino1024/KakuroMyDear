package Views

import java.util

import Models.BoardSize
import Util.Settings
import ViewGenerator.{generateButton, generateCaption}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.scene.layout.{GridPane, HBox}
import javafx.scene.text.Text
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{RadioButton, Toggle, ToggleGroup}

import scala.collection.mutable

class IntroView extends GenericView {
  private val actionButtonHandlers = new mutable.HashMap[IntroButton, EventHandler[ActionEvent]]()

  def injectActionButtonHandler(introButton: IntroButton, buttonEventHandler: EventHandler[ActionEvent]): Unit = {
    actionButtonHandlers.update(introButton, buttonEventHandler)
  }

  def generateActionButton(introButton: IntroButton): HBox = {
    actionButtonHandlers.get(introButton) match {
      case Some(handler) =>
        generateButton(introButton.name, handler)
      case None => throw new Exception("The " + introButton.name.toLowerCase + " handler hasn't been installed.")
    }
  }

  def createText(inputText: String): HBox = {
    val text = new Text(inputText)
    text.setId("IntroText")

    val container = new HBox(text)
    container.setId("ButtonContainer")

    container
  }

  def generateRadioButtons(): HBox = {
    val root = new HBox
    root.setId("IntroSizeChooser")
    root.setSpacing(80f)

    val boardButtons = new ToggleGroup
    boardButtons.selectedToggleProperty().addListener(new ChangeListener[Toggle] {
      override def changed(observable: ObservableValue[_ <: Toggle], oldValue: Toggle, newValue: Toggle): Unit = {
        if (boardButtons.getSelectedToggle != null) {
          val chosenButton = boardButtons.getSelectedToggle.asInstanceOf[RadioButton]
          println(chosenButton.getText)
          val chosenName = chosenButton.getText.toUpperCase
          val chosenSize = BoardSize.values.toList.filter({ boardSize => boardSize.toString == chosenName }).head
          Settings.boardSize = chosenSize
        }
      }
    })

    val buttonsArrayList = new util.ArrayList[RadioButton]
    for (boardSize <- BoardSize.values) {
      val radioButton = new RadioButton(boardSize.toString.toLowerCase.capitalize)
      radioButton.setToggleGroup(boardButtons)
      buttonsArrayList.add(radioButton)
    }
    buttonsArrayList.get(0).setSelected(true)
    Settings.boardSize = BoardSize.SMALL

    root.getChildren.addAll(buttonsArrayList)

    root
  }

  def generateIntroLayout(): GridPane = {
    val gridPane = new GridPane

    gridPane.add(generateCaption("KAKURO MY DEAR!", TitleCaption), 0, 0)
    gridPane.add(generateActionButton(Play), 0, 1)
    gridPane.add(generateActionButton(Scores), 0, 2)
    gridPane.add(generateActionButton(GameQuit), 0, 3)
    gridPane.add(generateRadioButtons(), 0, 4)
    gridPane.add(generateCaption("Made by Jarek & Mateusz", MinorCaption), 0, 5)

    gridPane
  }

  override def generateScene: Scene = {
    val root = generateIntroLayout()
    root.setId("App")
    val scene = new Scene(root)

    scene
  }

}