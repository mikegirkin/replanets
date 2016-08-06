package replanets.ui

import replanets.common.Constants
import replanets.model.Game
import replanets.ui.ScalafxHelpers.loadController

import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, Pane, VBox}
import scalafxml.core.macros.sfxml
/**
  * Created by mgirkin on 06/08/2016.
  */
trait IPlanetInfoView {
  def rootPane: VBox
  def setGameModel(model: Game): IPlanetInfoView
  def setPlanetId(turnId: Int, planetId: Int)
}

@sfxml
class PlanetInfoView(
  val rootPane: VBox,
  private val lblName: Label,
  private val lblWhen: Label,
  private val lblOwner: Label,
  private val lblFcode: Label,
  private val lblClimate: Label,
  private val pnNatives: Pane,
  private val pnColonists: Pane,
  val lblColonistPopulation: Label,
  val lblColonistTax: Label,
  val lblColonistHappiness: Label,
  val lblNatives: Label,
  val lblGovernment: Label,
  val lblPopulation: Label,
  val lblTax: Label,
  val lblHappiness: Label,

  val gridColonists: GridPane,
  val gridNatives: GridPane
) extends IPlanetInfoView {

  var game:Game = null

  override def setGameModel(model: Game): IPlanetInfoView ={
    this.game = model
    this
  }

  override def setPlanetId(turnId: Int, planetId: Int): Unit = {
    gridNatives.visible = false
    gridColonists.visible = false

    val data = game.turnSeverData(turnId)
    lblName.text = s"${game.map.planets(planetId).name} ($planetId)"
    lblWhen.text = "(now)"
    data.planets.find(_.planetId == planetId).foreach(p => {
      lblOwner.text = game.races(p.ownerId - 1).shortname
      lblFcode.text = p.fcode
      lblClimate.text = p.temperature.toString

      if(p.nativeRace != 0) {
        lblNatives.text = Constants.natives(p.nativeRace)
        lblGovernment.text = Constants.nativeGovernments(p.nativeGovernment)
        lblPopulation.text = s"${p.nativeClans} cl"
        lblTax.text = s"${p.nativeTax} %"
        lblHappiness.text = s"${p.nativeHappiness} %"
        gridNatives.visible = true
      }

      lblColonistPopulation.text = s"${p.colonistClans} cl"
      lblColonistTax.text = s"${p.colonistTax} %"
      lblColonistHappiness.text = s"${p.colonistHappiness} %"
      gridColonists.visible = true
    })
  }
}