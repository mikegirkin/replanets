package replanets.ui


import replanets.common.{Constants, IonStorm}

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
  private val lblCenter: Label,
  private val lblVoltage: Label,
  private val lblClass: Label,
  private val lblDynamic: Label,
  private val lblRadius: Label,
  private val lblSpeed: Label,
  private val lblHeading: Label
) extends IIonStormInformationView {

  def setData(storm: IonStorm) = {
    lblId.text = storm.id.toString
    lblCenter.text = s"( ${storm.x}, ${storm.y} )"
    lblHeading.text = storm.heading.toString
    lblRadius.text = storm.radius.toString
    lblSpeed.text = storm.warp.toString
    lblVoltage.text = s"${storm.voltage} MeV"
    lblClass.text = Constants.StormCategoryText(storm.category)
    lblDynamic.text = if(storm.isGrowing) "and growing" else "and weakening"
  }
}
