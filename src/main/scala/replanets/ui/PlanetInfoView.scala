package replanets.ui

import replanets.common._
import replanets.model.Game
import replanets.ui.actions.Actions
import replanets.ui.viewmodels.{PlanetInfoVM, ViewModel}

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml
/**
  * Created by mgirkin on 06/08/2016.
  */
trait IPlanetInfoView {
  def rootPane: VBox
  def setPlanet(turnId: TurnId, planetId: Int)
}

@sfxml
class PlanetInfoView(
  val rootPane: VBox,
  val lblName: Label,
  val lblPlanetId: Label,
  val lblWhen: Label,
  val lblOwner: Label,
  val lblFcode: Label,
  val lblClimate: Label,
  val lblColonistPopulation: Label,
  val lblColonistTax: Label,
  val lblColonistIncome: Label,
  val lblColonistHappiness: Label,
  val lblColonistHappinessChange: Label,
  val lblNatives: Label,
  val lblGovernment: Label,
  val lblPopulation: Label,
  val lblTaxPerformance: Label,
  val lblTax: Label,
  val lblNativesIncome: Label,
  val lblHappiness: Label,
  val lblNativeHappinessChange: Label,
  val lblMines: Label,
  val lblFactories: Label,
  val lblDefenses: Label,
  val lblSupplies: Label,
  val lblMoney: Label,

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

  val edFcode: TextField,

  val pnColonists: Pane,
  val pnNatives: Pane,
  val pnGeneralInfo: Pane,
  val pnStructures: Pane,
  val pnMinerals: Pane,


  val game: Game,
  val viewModel: ViewModel,
  val commands: Actions
) extends IPlanetInfoView {

  viewModel.objectChanged += { mapObject => handleObjectChanged(mapObject) }

  override def setPlanet(turnId: TurnId, planetId: Int): Unit = {
    pnNatives.visible = false
    pnColonists.visible = false
    pnGeneralInfo.visible = false
    pnStructures.visible = false
    pnMinerals.visible = false
    btnStarbase.visible = false

    val data = game.turnInfo(turnId).stateAfterCommands
    lblPlanetId.text = planetId.toString
    lblName.text = game.map.planets(planetId).name
    lblWhen.text = "(now)"
    lblOwner.text = "Planet"
    data.planets.get(PlanetId(planetId)).foreach(p => {
      val vm = new PlanetInfoVM(game, viewModel.turnShown, p.planetId)

      if(vm.ownerId != 0) lblOwner.text = s"${game.races(vm.ownerId - 1).adjective} planet"
      lblFcode.text = vm.fcode.value
      edFcode.text = vm.fcode.value
      lblClimate.text = vm.temperature.toString
      pnGeneralInfo.visible = true

      if(vm.nativeRace != NativeRace.None) {
        lblNatives.text = p.nativeRace.name
        lblGovernment.text = p.nativeGovernment.name
        lblTaxPerformance.text = s"${p.nativeGovernment.taxPercentage}%"
        lblPopulation.text = s"${vm.nativeClans} cl"
        lblTax.text = s"${vm.nativeTax} %"
        lblNativesIncome.text = s"${vm.nativeIncome}"
        lblHappiness.text = s"${vm.nativeHappiness} %"
        lblNativeHappinessChange.text = s"${vm.nativeHappinessChange}"
        pnNatives.visible = true
      }

      lblColonistPopulation.text = s"${vm.colonistClans} cl"
      lblColonistTax.text = s"${vm.colonistTax} %"
      lblColonistIncome.text = s"${vm.colonistIncome}"
      lblColonistHappiness.text = s"${vm.colonistHappiness} %"
      lblColonistHappinessChange.text = s"${vm.colonistHappinessChange}"
      pnColonists.visible = true

      lblMines.text = s"${vm.minesNumber} / ${game.formulas.maxMines(vm.colonistClans)}"
      lblFactories.text = s"${vm.factoriesNumber} / ${game.formulas.maxFactories(vm.colonistClans)}"
      lblDefenses.text = s"${vm.defencesNumber} / ${game.formulas.maxDefences(vm.colonistClans)}"
      pnStructures.visible = true

      lblSupplies.text = s"${vm.supplies}"
      lblMoney.text =s"${vm.money}"

      val miningRate = (density: Int) => game.formulas.miningRate(density, vm.minesNumber, game.playingRace, vm.nativeRace)

      lblNeuMined.text = vm.surfaceMinerals.neutronium.toString
      lblNeuCore.text = vm.coreMinerals.neutronium.toString
      lblNeuDensity.text = s"${miningRate(vm.densityMinerals.neutronium)} / ${vm.densityMinerals.neutronium}"
      lblTriMined.text = p.surfaceMinerals.tritanium.toString
      lblTriCore.text = p.coreMinerals.tritanium.toString
      lblTriDensity.text = s"${miningRate(vm.densityMinerals.tritanium)} / ${vm.densityMinerals.tritanium}"
      lblDurMined.text = p.surfaceMinerals.duranium.toString
      lblDurCore.text = p.coreMinerals.duranium.toString
      lblDurDensity.text = s"${miningRate(vm.densityMinerals.duranium)} / ${vm.densityMinerals.duranium}"
      lblMolMined.text = p.surfaceMinerals.molybdenium.toString
      lblMolCore.text = p.coreMinerals.molybdenium.toString
      lblMolDensity.text = s"${miningRate(vm.densityMinerals.molybdenium)} / ${vm.densityMinerals.molybdenium}"
      pnMinerals.visible = true
    })
    data.bases.get(PlanetId(planetId)).foreach{ _ =>
      btnStarbase.visible = true
    }
  }

  private def handleObjectChanged(mapObject: MapObject): Unit = {
    mapObject match {
      case MapObject.Planet(id, coords, displayName) => setPlanet(viewModel.turnShown, id)
      case _ =>
    }
  }

  def onBaseButton(e: ActionEvent) = commands.selectStarbase.execute()

  def onRandomFcodeButton(e: ActionEvent) = commands.setFcode.execute(Fcode.random())

  def onFcodeLabelClicked(e: MouseEvent) = {
    lblFcode.visible = false
    edFcode.visible = true
    edFcode.requestFocus()
  }

  def onEdFcodeAction(e: ActionEvent) = {
    println(s"${e.eventType}")
    val fcode = Fcode.tryConvert(edFcode.text.value)
    fcode.foreach { f =>
      commands.setFcode.execute(f)
      lblFcode.visible = true
      edFcode.visible = false
    }
  }

  def cancelFcodeEditing() = {
    if(edFcode.visible.value) {
      edFcode.text = lblFcode.text.value
      lblFcode.visible = true
      edFcode.visible = false
    }
  }

  def onEdFcodeKeyPressed(e: KeyEvent) = {
    if(e.code == KeyCode.Escape) cancelFcodeEditing()
  }
}