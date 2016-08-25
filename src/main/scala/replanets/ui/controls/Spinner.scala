package replanets.ui.controls

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox, Pane}
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
  val buttons = new SpinnerButtons(onDiff)

  alignment = Pos.CenterLeft

  columnConstraints = Seq(
    new ColumnConstraints(width = minLabelWidth)
  )

  add(lblValue, 0, 0)
  add(buttons, 1, 0)
}