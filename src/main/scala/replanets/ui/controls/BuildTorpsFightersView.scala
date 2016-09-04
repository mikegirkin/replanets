package replanets.ui.controls

import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane

class BuildTorpsFightersView(

) extends GridPane {
  val torps = (0 until 10).map { idx =>
    (
      idx,
      Label(s"Torps ${idx}"),
      Label("???"),
      new SpinnerButtons((_) => Unit)
    )
  }
  torps.foreach { case (idx, lbl1, lbl2, spinner) =>
    this.add(lbl1, 0, idx)
    this.add(lbl2, 0, idx)
    this.add(spinner, 0, idx)
  }
}
