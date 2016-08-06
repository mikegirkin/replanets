package replanets.ui

import replanets.common.{Constants, PlanetRecord}

import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 06/08/2016.
  */

trait INativesView {
  def root: GridPane
  def setData(data: PlanetRecord): Unit
}

@sfxml
class NativesView(
  val root: GridPane,
  private val lblNatives: Label,
  private val lblGovernment: Label,
  private val lblPopulation: Label,
  private val lblTax: Label,
  private val lblHappiness: Label
) extends INativesView {

  lblNatives.text = "dlfkjhsdlfkjhsf"

  def setData(data: PlanetRecord): Unit = {
    lblNatives.text = Constants.natives(data.nativeRace)
    lblGovernment.text = Constants.nativeGovernments(data.nativeGovernment)
    lblPopulation.text = s"${data.nativeClans} cl"
    lblTax.text = data.nativeTax.toString
    lblHappiness.text = data.nativeHappiness.toString
  }
}