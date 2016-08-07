package replanets.ui

import replanets.common.Constants
import replanets.model.Game
import replanets.ui.commands.Commands

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml
/**
  * Created by mgirkin on 06/08/2016.
  */
trait IPlanetInfoView {
  def rootPane: VBox
  def setPlanet(turnId: Int, planetId: Int)
}

@sfxml
class PlanetInfoView(
  val rootPane: VBox,
  private val lblName: Label,
  private val lblWhen: Label,
  private val lblOwner: Label,
  private val lblFcode: Label,
  private val lblClimate: Label,
  val lblColonistPopulation: Label,
  val lblColonistTax: Label,
  val lblColonistHappiness: Label,
  val lblNatives: Label,
  val lblGovernment: Label,
  val lblPopulation: Label,
  val lblTax: Label,
  val lblHappiness: Label,
  val lblMines: Label,
  val lblFactories: Label,
  val lblDefenses: Label,

  val lblNeuMined: Label,
  val lblNeuCore: Label,
  val lblNeuDensity: Label,
  val lblTriMined: Label,
  val lblTriCore: Label,
  val lblTriDensity: Label,
  val lblDurMined: Label,
  val lblDurCore: Label,
  val lblDurDensity: Label,
  val lblMolMined: Label,
  val lblMolCore: Label,
  val lblMolDensity: Label,

  val btnStarbase: Button,

  val pnColonists: Pane,
  val pnNatives: Pane,
  val pnGeneralInfo: Pane,
  val pnStructures: Pane,
  val pnMinerals: Pane,

  val game: Game,
  val commands: Commands
) extends IPlanetInfoView {

  override def setPlanet(turnId: Int, planetId: Int): Unit = {
    pnNatives.visible = false
    pnColonists.visible = false
    pnGeneralInfo.visible = false
    pnStructures.visible = false
    pnMinerals.visible = false
    btnStarbase.visible = false

    val data = game.turnSeverData(turnId)
    lblName.text = s"${game.map.planets(planetId).name} ($planetId)"
    lblWhen.text = "(now)"
    data.planets.find(_.planetId == planetId).foreach(p => {
      lblOwner.text = game.races(p.ownerId - 1).shortname
      lblFcode.text = p.fcode
      lblClimate.text = p.temperature.toString
      pnGeneralInfo.visible = true

      if(p.nativeRace != 0) {
        lblNatives.text = Constants.natives(p.nativeRace)
        lblGovernment.text = Constants.nativeGovernments(p.nativeGovernment)
        lblPopulation.text = s"${p.nativeClans} cl"
        lblTax.text = s"${p.nativeTax} %"
        lblHappiness.text = s"${p.nativeHappiness} %"
        pnNatives.visible = true
      }

      lblColonistPopulation.text = s"${p.colonistClans} cl"
      lblColonistTax.text = s"${p.colonistTax} %"
      lblColonistHappiness.text = s"${p.colonistHappiness} %"
      pnColonists.visible = true

      lblMines.text = s"${p.minesNumber} / ${game.formulas.maxMines(p.colonistClans)}"
      lblFactories.text = s"${p.factoriesNumber} / ${game.formulas.maxFactories(p.colonistClans)}"
      lblDefenses.text = s"${p.defencesNumber} / ${game.formulas.maxDefences(p.colonistClans)}"
      pnStructures.visible = true

      val miningRate = (density: Int) => game.formulas.miningRate(density, p.minesNumber, game.playingRace, p.nativeRace)

      lblNeuMined.text = p.surfaceMinerals.neutronium.toString
      lblNeuCore.text = p.coreMinerals.neutronium.toString
      lblNeuDensity.text = s"${miningRate(p.densityMinerals.neutronium)} / ${p.densityMinerals.neutronium}"
      lblTriMined.text = p.surfaceMinerals.tritanium.toString
      lblTriCore.text = p.coreMinerals.tritanium.toString
      lblTriDensity.text = s"${miningRate(p.densityMinerals.tritanium)} / ${p.densityMinerals.tritanium}"
      lblDurMined.text = p.surfaceMinerals.duranium.toString
      lblDurCore.text = p.coreMinerals.duranium.toString
      lblDurDensity.text = s"${miningRate(p.densityMinerals.duranium)} / ${p.densityMinerals.duranium}"
      lblMolMined.text = p.surfaceMinerals.molybdenium.toString
      lblMolCore.text = p.coreMinerals.molybdenium.toString
      lblMolDensity.text = s"${miningRate(p.densityMinerals.molybdenium)} / ${p.densityMinerals.molybdenium}"
      pnMinerals.visible = true
    })
    data.bases.find(_.baseId == planetId).foreach{ _ =>
      btnStarbase.visible = true
    }
  }

  def onBaseButton(e: ActionEvent) = commands.selectStarbase.execute()
}