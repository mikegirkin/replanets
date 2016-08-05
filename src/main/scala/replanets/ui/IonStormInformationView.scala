package replanets.ui


import replanets.common.IonStorm

import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 05/08/2016.
  */
trait IIonStormInformationView {
  val rootPane: GridPane
  def setData(storm: IonStorm): Unit
}

@sfxml
class IonStormInformationView(
  val rootPane: GridPane,
  private val lblId: Label,
  private val lblVoltage: Label,
  private val lblRadius: Label,
  private val lblSpeed: Label,
  private val lblHeading: Label
) extends IIonStormInformationView {

  def setData(storm: IonStorm) = {
    lblId.text = storm.id.toString
    lblHeading.text = storm.heading.toString
    lblRadius.text = storm.radius.toString
    lblSpeed.text = storm.warp.toString
    lblVoltage.text = s"${storm.voltage.toString} ${if (storm.voltage%2 == 1) "growing" else "weakening"}"
  }
}
