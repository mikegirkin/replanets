package replanets.ui.controls

import scalafx.beans.binding.StringBinding
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import scalafx.scene.text.TextAlignment

class Spinner(
  binding: StringBinding,
  onDiff: (Int) => Unit,
  minLabelWidth: Int = 30
) extends GridPane {

  val lblValue = new Label {
    text <== binding
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