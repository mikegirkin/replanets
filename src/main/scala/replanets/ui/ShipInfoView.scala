package replanets.ui

import replanets.common._
import replanets.model.{CargoHold, Game}
import replanets.ui.actions.Actions
import replanets.ui.controls.{CargoTransferView, Spinner}
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{GridPane, HBox, Pane}
import scalafx.stage.Popup
import scalafxml.core.macros.sfxml

trait IShipInfoView {
  def rootPane: Pane
  def setData(shipId: ShipId): Unit
}

@sfxml
class ShipInfoView(
  val rootPane: Pane,
  val lblShipId: Label,
  val lblShipOwningRace: Label,
  val lblShipName: Label,
  val lblHullName: Label,
  val lblShipCoords: Label,
  val lblDestination: Label,
  val lblFuel: Label,
  val lblBurn: Label,
  val lblMass: Label,
  val lblFcode: Label,
  val lblCrew: Label,
  val lblDamage: Label,
  val lblMission: Label,
  val lblEnemy: Label,
  val lblEquipEngines: Label,
  val lblEquipBeams: Label,
  val lblEquipLaunchers: Label,
  val lblCargo: Label,
  val lblNeu: Label,
  val lblTri: Label,
  val lblDur: Label,
  val lblMol: Label,
  val lblSupplies: Label,
  val lblClans: Label,
  val lblMoney: Label,
  val lblTorps: Label,

  val edFcode: TextField,

  val hbWarpPlaceholder: HBox,
  val gpCargo: GridPane,

  val game: Game,
  val viewModel: ViewModel,
  val actions: Actions
) extends IShipInfoView {

  viewModel.objectChanged += handleObjectChanged

  val ship = ObjectProperty[Option[OwnShip]](None)
  val transferSource = ObjectProperty(CargoHold.zero)
  val transferDestination = ObjectProperty(CargoHold.zero)
  var transferringToPlanet: Option[PlanetId] = None

  val warpSpinner = new Spinner(
    createStringBinding(() => ship.value.map(_.warp).getOrElse(0).toString, ship),
    (delta) => ship.value.foreach { s =>
      actions.setShipWarp(s, s.warp + delta)
    },
    minLabelWidth = 15
  )

  hbWarpPlaceholder.children = warpSpinner

  val cargoTransferControl = new CargoTransferView(
    transferSource,
    transferDestination,
    onChange = transfer => {
      val planet = game.turnInfo(viewModel.turnShown).stateAfterCommands.planets(transferringToPlanet.get)
      actions.transferShipToPlanet(ship.value.get, planet, transfer)
    }
  ) {
    maxWidth <== rootPane.width
    minWidth <== rootPane.width
  }

  val transferPopup = new Popup {
    autoHide = true
    content.add(cargoTransferControl)
  }

  private def setTransferDestination() = {
    transferringToPlanet.foreach { planetId =>
      val planet = game.turnInfo(viewModel.turnShown).stateAfterCommands.planets(planetId)
      val planetBeforeCommands = game.turnInfo(viewModel.turnShown).initialState.planets(planetId)
      transferDestination.value =
        if(planet.ownerId == game.playingRace)
          planet.cargoHold
        else {
          planet.cargoHold.copy(_cargo = planet.cargoHold.cargo.minus(planetBeforeCommands.cargoHold.cargo))
        }
    }
  }

  def setData(shipId: ShipId) = {
    val newShip = game.turnInfo(viewModel.turnShown).stateAfterCommands.ships(shipId).asInstanceOf[OwnShip]
    ship.value = Some(newShip)
    transferSource.value = newShip.cargoHold
    setTransferDestination()

    lblShipId.text = newShip.id.value.toString
    lblShipOwningRace.text = if(newShip.owner.value>0) {
      game.races(newShip.owner.value - 1).adjective
    } else {
      "Unknown"
    }
    lblShipName.text = newShip.name
    lblHullName.text = newShip.hull.name
    lblShipCoords.text = s"(${newShip.x}, ${newShip.y})"
    lblDestination.text = s"(${newShip.x + newShip.xDistanceToWaypoint}, ${newShip.y + newShip.yDistanceToWaypoint})"
    lblFuel.text = s"${newShip.minerals.neutronium} / ${newShip.hull.fuelTankSize}"
    val fuelBurn = game.specs.formulas.fuelBurn(
      newShip.engines, newShip.warp, newShip.fullMass,
      newShip.xDistanceToWaypoint, newShip.yDistanceToWaypoint, game.specs.isGravitonic(newShip.hull.id))
    lblBurn.text = fuelBurn.toString
    lblMass.text = s"${newShip.fullMass}"
    lblFcode.text = newShip.fcode.value
    lblCrew.text = s"${newShip.crew} / ${newShip.hull.crewSize}"
    lblDamage.text = s"${newShip.damage} %"
    lblMission.text = game.specs.missions.get(newShip.mission)
    lblEnemy.text = if(newShip.primaryEnemy != 0)  game.races(newShip.primaryEnemy - 1).shortname else "(none)"
    lblEquipEngines.text = newShip.engines.name
    lblEquipBeams.text = newShip.beams.map { b => s"${newShip.numberOfBeams} - ${b.name}" }.getOrElse("")
    lblEquipLaunchers.text = newShip.torpsType.map { tt => s"${newShip.numberOfTorpLaunchers} - ${tt.name}" }.getOrElse("")
    lblCargo.text =s"${newShip.cargoMass.toString} / ${newShip.hull.cargo}"
    lblNeu.text = newShip.minerals.neutronium.toString
    lblTri.text = newShip.minerals.tritanium.toString
    lblDur.text = newShip.minerals.duranium.toString
    lblMol.text = newShip.minerals.molybdenium.toString
    lblSupplies.text = newShip.supplies.toString
    lblClans.text = newShip.colonistClans.toString
    lblMoney.text = newShip.money.toString
    lblTorps.text = newShip.torpsFightersLoaded.toString
  }

  private def handleObjectChanged(mapObject: MapObject): Unit = {
    mapObject match {
      case MapObject.OwnShip(id, coords, displayName) if id == ship.value.map(_.id.value).getOrElse(0) =>
        setData(ShipId(id))
      case _ =>
    }
  }

  def onRandomFcodeButton(e: ActionEvent) = {
    ship.value.foreach(s =>
      actions.setShipFcode(s, Fcode.random())
    )
  }

  def onFcodeLabelClicked(e: MouseEvent) = {
    lblFcode.visible = false
    edFcode.visible = true
    edFcode.requestFocus()
  }

  def onEdFcodeAction(e: ActionEvent) = {
    for(
      s <- ship.value;
      fcode <- Fcode.tryConvert(edFcode.text.value)
    ) {
      actions.setShipFcode(s, fcode)
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

  def handleShipPlanetTransferButton(e: ActionEvent): Unit = {
    for(
      ship <- ship.value;
      planet <- game.turnInfo(viewModel.turnShown).stateAfterCommands.planets.values.find { planet => planet.mapData.coords == ship.coords }
    ) {
      transferringToPlanet = Some(planet.id)
      setTransferDestination()
      val moneyTransferAvailable = planet.ownerId == game.playingRace
      val torpsTransferAvailable = {
        val starbase = game.turnInfo(viewModel.turnShown).initialState.bases.get(planet.id)
        ship.numberOfTorpLaunchers > 0 && starbase.isDefined
      }
      val fightersTransferAvailable = {
        val starbase = game.turnInfo(viewModel.turnShown).initialState.bases.get(planet.id)
        ship.fighterBays > 0 && starbase.isDefined
      }
      cargoTransferControl.setData(
        moneyTransferAvailable,
        torpsTransferAvailable,
        fightersTransferAvailable
      )
      val point = gpCargo.localToScreen(0, 0)
      transferPopup.show(gpCargo.getScene.getWindow, point.getX, point.getY)
    }
  }

  def hidePopup(): Unit = {
    transferPopup.hide()
    transferringToPlanet = None
  }
}