package replanets.ui

import replanets.common.PlanetRecord

import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 06/08/2016.
  */
trait IColonistsView {
  def root: GridPane
  def setData(planet: PlanetRecord): Unit
}

@sfxml
class ColonistsView(
  val root: GridPane,
  val lblPopulation: Label,
  val lblTax: Label,
  val lblHappiness: Label
) extends IColonistsView {

  def setData(planet: PlanetRecord): Unit ={
    lblPopulation.text = s"${planet.colonistClans} cl"
    lblHappiness.text = planet.colonistHappiness.toString
    lblTax.text = planet.colonistHappiness.toString
  }
}
