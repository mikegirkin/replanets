package replanets.ui

import replanets.common._
import replanets.model.Game
import replanets.ui.actions.Actions
import replanets.ui.controls.Spinner
import replanets.ui.viewmodels.{PlanetInfoVM, ViewModel}

import scalafx.Includes._
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, CheckBox, Label, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{GridPane, Pane, VBox}
import scalafxml.core.macros.sfxml

trait IPlanetInfoView {
  def rootPane: VBox
  def setPlanet(turnId: TurnId, planetId: Int)
}

@sfxml
class PlanetInfoView(
  val rootPane: VBox,
  val cbDone: CheckBox,
  val lblName: Label,
  val lblPlanetId: Label,
  val lblWhen: Label,
  val lblOwner: Label,
  val lblFcode: Label,
  val lblClimate: Label,
  val lblColonistPopulation: Label,
  val lblColonistIncome: Label,
  val lblColonistHappiness: Label,
  val lblColonistHappinessChange: Label,
  val lblNatives: Label,
  val lblGovernment: Label,
  val lblPopulation: Label,
  val lblTaxPerformance: Label,
  val lblNativesIncome: Label,
  val lblHappiness: Label,
  val lblNativeHappinessChange: Label,
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
  val btnRandomFcode: Button,

  val edFcode: TextField,

  val pnColonists: Pane,
  val gpColonists: GridPane,
  val pnNatives: Pane,
  val gpNatives: GridPane,
  val pnGeneralInfo: Pane,
  val pnStructures: Pane,
  val gpStructures: GridPane,
  val pnMinerals: Pane,

  val game: Game,
  val viewModel: ViewModel,
  val commands: Actions
) extends IPlanetInfoView {

  viewModel.objectChanged += {
    mapObject => handleObjectChanged(mapObject)
  }

  val colonistTax = IntegerProperty(0)
  val nativeTax = IntegerProperty(0)
  val factories = IntegerProperty(0)
  val mines = IntegerProperty(0)
  val defences = IntegerProperty(0)
  val planet = ObjectProperty[Option[PlanetInfoVM]](None)

  val colonistTaxSpinner = new Spinner(
    createStringBinding(() => s"${colonistTax.value} %", colonistTax, planet),
    (delta) => {
      planet.value.foreach( p =>
        commands.changeColonistTax(p.planetRecord, colonistTax.value + delta)
      )
    },
    minLabelWidth = 45
  )

  val nativeTaxSpinner = new Spinner(
    createStringBinding(() => s"${nativeTax.value} %", nativeTax, planet),
    (delta) => {
      planet.value.foreach( p =>
        commands.changeNativeTax(p.planetRecord, nativeTax.value + delta)
      )
    },
    minLabelWidth = 45
  )

  val factoriesSpinner = new Spinner(
    createStringBinding(() => planet.value.map( p =>
      s"${p.factoriesNumber} / ${p.maxFactories}"
    ).getOrElse(""), factories, planet),
    onDiff = (delta) => {
      planet.value.foreach( p =>
        commands.buildFactories(p.planetRecord, p.factoriesNumber + delta)
      )
    },
    minLabelWidth = 70
  )

  val minesSpinner = new Spinner(
    createStringBinding(() => planet.value.map(p => s"${p.minesNumber} / ${p.maxMines}").getOrElse(""), mines, planet),
    onDiff = (delta) => {
      planet.value.foreach( p =>
        commands.buildMines(p.planetRecord, p.minesNumber + delta)
      )
    },
    minLabelWidth = 70
  )

  val defencesSpinner = new Spinner(
    createStringBinding(() => planet.value.map(p => s"${p.defencesNumber} / ${p.maxDefences}").getOrElse(""), defences, planet),
    onDiff = (delta) => {
      planet.value.foreach( p =>
        commands.buildDefences(p.planetRecord, p.defencesNumber + delta)
      )
    },
    minLabelWidth = 70
  )

  gpColonists.add(colonistTaxSpinner, 1, 1)
  gpNatives.add(nativeTaxSpinner, 1, 1)
  gpStructures.add(minesSpinner, 1, 0)
  gpStructures.add(factoriesSpinner, 1, 1)
  gpStructures.add(defencesSpinner, 1, 2)

  override def setPlanet(turnId: TurnId, planetId: Int): Unit = {
    val data = game.turnInfo(turnId).stateAfterCommands
    val planetRecord = data.planets.get(PlanetId(planetId)).map(_ => new PlanetInfoVM(game, viewModel.turnShown, PlanetId(planetId)))
    planet.update(planetRecord)

    pnNatives.visible = false
    pnColonists.visible = false
    pnGeneralInfo.visible = false
    pnStructures.visible = false
    pnMinerals.visible = false
    btnStarbase.visible = false

    lblPlanetId.text = planetId.toString
    lblName.text = game.specs.map.planets(planetId - 1).name
    lblWhen.text = "(now)"
    lblOwner.text = "Planet"
    planetRecord.foreach( vm => {
      val editable = vm.ownerId == game.playingRace.value
      if(editable) {
        colonistTaxSpinner.visible = true
        nativeTaxSpinner.visible = true
        factoriesSpinner.visible = true
        minesSpinner.visible = true
        defencesSpinner.visible = true
        lblColonistHappinessChange.visible = true
        lblNativeHappinessChange.visible = true
        lblColonistIncome.visible = true
        lblNativesIncome.visible = true
        cbDone.visible = true
        btnRandomFcode.visible = true
      } else {
        colonistTaxSpinner.visible = false
        nativeTaxSpinner.visible = false
        factoriesSpinner.visible = false
        minesSpinner.visible = false
        defencesSpinner.visible = false
        lblColonistHappinessChange.visible = false
        lblNativeHappinessChange.visible = false
        lblColonistIncome.visible = false
        lblNativesIncome.visible = false
        cbDone.visible = false
        btnRandomFcode.visible = false
      }
      if(vm.ownerId != 0) lblOwner.text = s"${game.races(vm.ownerId - 1).adjective} planet"
      lblFcode.text = vm.fcode.value
      edFcode.text = vm.fcode.value
      lblClimate.text = vm.temperature.toString
      pnGeneralInfo.visible = true

      if(vm.nativeRace != NativeRace.None) {
        lblNatives.text = vm.nativeRace.name
        lblGovernment.text = vm.nativeGovernment.name
        lblTaxPerformance.text = s"${vm.nativeGovernment.taxPercentage}%"
        lblPopulation.text = s"${vm.nativeClans} cl"
        nativeTax.value = vm.nativeTax
        lblNativesIncome.text = s"${vm.nativeIncome}"
        lblHappiness.text = s"${vm.nativeHappiness} %"
        lblNativeHappinessChange.text = s"${vm.nativeHappinessChange}"
        pnNatives.visible = true
      }

      lblColonistPopulation.text = s"${vm.colonistClans} cl"
      colonistTax.value = vm.colonistTax
      lblColonistIncome.text = s"${vm.colonistIncome}"
      lblColonistHappiness.text = s"${vm.colonistHappiness} %"
      lblColonistHappinessChange.text = s"${vm.colonistHappinessChange}"
      pnColonists.visible = true

      mines.value = vm.minesNumber
      factories.value = vm.factoriesNumber
      defences.value = vm.defencesNumber
      pnStructures.visible = true

      lblSupplies.text = s"${vm.supplies}"
      lblMoney.text =s"${vm.money}"

      val miningRate = (density: Int) => game.specs.formulas.miningRate(density, vm.minesNumber, game.playingRace, vm.nativeRace)

      lblNeuMined.text = vm.surfaceMinerals.neutronium.toString
      lblNeuCore.text = vm.coreMinerals.neutronium.toString
      lblNeuDensity.text = s"${miningRate(vm.densityMinerals.neutronium)} / ${vm.densityMinerals.neutronium}"
      lblTriMined.text = vm.surfaceMinerals.tritanium.toString
      lblTriCore.text = vm.coreMinerals.tritanium.toString
      lblTriDensity.text = s"${miningRate(vm.densityMinerals.tritanium)} / ${vm.densityMinerals.tritanium}"
      lblDurMined.text = vm.surfaceMinerals.duranium.toString
      lblDurCore.text = vm.coreMinerals.duranium.toString
      lblDurDensity.text = s"${miningRate(vm.densityMinerals.duranium)} / ${vm.densityMinerals.duranium}"
      lblMolMined.text = vm.surfaceMinerals.molybdenium.toString
      lblMolCore.text = vm.coreMinerals.molybdenium.toString
      lblMolDensity.text = s"${miningRate(vm.densityMinerals.molybdenium)} / ${vm.densityMinerals.molybdenium}"
      pnMinerals.visible = true
    })
    data.bases.get(PlanetId(planetId)).foreach{ _ =>
      btnStarbase.visible = true
    }
  }

  private def handleObjectChanged(mapObject: MapObject): Unit = {
    mapObject match {
      case MapObject.Planet(id, coords, displayName) if id == planet.value.map(_.planetRecord.id.value).getOrElse(0) =>
        setPlanet(viewModel.turnShown, id)
      case _ =>
    }
  }

  def onBaseButton(e: ActionEvent) = commands.selectStarbase.execute()

  def onRandomFcodeButton(e: ActionEvent) = {
    planet.value.foreach { p =>
      commands.setPlanetFcode(p.planetRecord, Fcode.random())
    }
  }

  def onFcodeLabelClicked(e: MouseEvent) = {
    lblFcode.visible = false
    edFcode.visible = true
    edFcode.requestFocus()
  }

  def onEdFcodeAction(e: ActionEvent) = {
    for(
      p <- planet.value;
      fcode <- Fcode.tryConvert(edFcode.text.value)
    ) {
      commands.setPlanetFcode(p.planetRecord, fcode)
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