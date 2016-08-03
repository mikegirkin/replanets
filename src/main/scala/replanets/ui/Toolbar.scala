package replanets.ui

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.HBox

/**
  * Created by mgirkin on 29/07/2016.
  */
abstract class Toolbar extends HBox {

  def onMessages(e: ActionEvent): Unit
  def onMap(e: ActionEvent): Unit

  padding = Insets(5)
  minWidth = 200
  children = Seq(
    new Button {
      text = "F1 - Map"
      onAction = onMap _
    },
    new Button {
      text = "F2 - Messages"
      onAction = onMessages _
    },
    new Button("F3 - Planets"),
    new Button("F4 - Ships"),
    new Button("F5 - Bases")
  )

}
