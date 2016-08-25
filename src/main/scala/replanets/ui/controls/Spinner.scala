package replanets.ui.controls

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import scalafx.scene.text.TextAlignment

class Spinner(
  valueToBind: IntegerProperty,
  onDiff: (Int) => Unit,
  formatter: (Int) => String = _.toString,
  minLabelWidth: Int = 30
) extends GridPane {

  val lblValue = new Label {
    text <== createStringBinding(() => formatter(valueToBind.value), valueToBind)
    alignmentInParent = Pos.CenterLeft
    alignment = Pos.CenterLeft
    textAlignment = TextAlignment.Left
  }
  val btnPlus = new Button {
    styleClass = Seq("inGridButton")
    text = "+"
    onMouseClicked = (e: MouseEvent) => handlePlusClicked(e)
  }
  val btnMinus = new Button {
    styleClass = Seq("inGridButton")
    text = "-"
    onMouseClicked = (e: MouseEvent) => handleMinusClicked(e)
  }

  alignment = Pos.CenterLeft

  columnConstraints = Seq(
    new ColumnConstraints(width = minLabelWidth)
  )

  add(lblValue, 0, 0)
  add(btnPlus, 1, 0)
  add(btnMinus, 2, 0)

  private def getDelta(e: MouseEvent) = {
    if(e.isControlDown) 10
    else if(e.isAltDown) 100
    else if(e.isShiftDown) 1000
    else 1
  }

  private def handlePlusClicked(e: MouseEvent) = {
    onDiff(getDelta(e))
  }

  private def handleMinusClicked(e: MouseEvent) = {
    onDiff(-getDelta(e))
  }
}
