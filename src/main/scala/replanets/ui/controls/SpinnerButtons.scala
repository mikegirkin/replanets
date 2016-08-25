package replanets.ui.controls

import scalafx.Includes._
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox

class SpinnerButtons(
  onDiff: (Int) => Unit
) extends HBox {

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

  children = Seq(
    btnMinus,
    btnPlus
  )

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
