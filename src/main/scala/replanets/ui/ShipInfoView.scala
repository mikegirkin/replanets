package replanets.ui

import replanets.common._
import replanets.model.{CargoHold, Game}
import replanets.ui.actions.Actions
import replanets.ui.controls.{CargoTransferView, ObjectsListView, Spinner}
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.{GridPane, HBox, Pane, VBox}
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
  val lblTorpsHeader: Label,
  val lblTorps: Label,

  val edFcode: TextField,
  val btnTransfer: Button,

  val hbWarpPlaceholder: HBox,
  val gpCargo: GridPane,

  val game: Game,
  val viewModel: ViewModel,
  val actions: Actions
) extends IShipInfoView {

  viewModel.objectChanged += handleObjectChanged

  val ship = ObjectProperty[Option[OwnShip]](None)

  val warpSpinner = new Spinner(
    createStringBinding(() => ship.value.map(_.warp).getOrElse(0).toString, ship),
    (delta) => ship.value.foreach { s =>
      actions.setShipWarp(s, s.warp + delta)
    },
    minLabelWidth = 15
  )

  hbWarpPlaceholder.children = warpSpinner

  val cargoTransferControl = new CargoTransferView(
    game,
    viewModel,
    actions
  ) {
    maxWidth <== rootPane.width
    minWidth <== rootPane.width
  }

  val transferPopup = new Popup {
    autoHide = true
    content.add(cargoTransferControl)
  }

  val targetSelectionControl = new ObjectsListView(
    mo => onTransferDestinationSelected(mo)
  )

  val transferTargetSelectionPopup = new Popup {
    autoHide = true

    val children = new VBox (
      new Label("Transfer to: "),
      targetSelectionControl
    )

    content.add(children)
  }

  def setData(shipId: ShipId) = {
    val newShip = game.turnInfo(viewModel.turnShown).stateAfterCommands.ships(shipId).asInstanceOf[OwnShip]
    ship.value = Some(newShip)

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
    lblTorps.visible = newShip.isCarrier || newShip.isLauncher
    lblTorpsHeader.text = if(newShip.isLauncher) "Torps:" else "Fighters:"
    lblTorpsHeader.visible = newShip.isCarrier || newShip.isLauncher
    btnTransfer.visible = MapObject.shipsAtCoords(game, viewModel.turnShown)(newShip.coords).size > 1
  }

  private def handleObjectChanged(mapObject: MapObject): Unit = {
    mapObject match {
      case MapObject.OwnShip(id, coords, displayName, _) if id == ship.value.map(_.id.value).getOrElse(0) =>
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

  private def showTransferPopup() = {
    val point = gpCargo.localToScreen(0, 0)
    transferPopup.show(gpCargo.getScene.getWindow, point.getX, point.getY)
  }

  def handleShipPlanetTransferButton(e: ActionEvent): Unit = {
    val sourceShip = ship.value.get
    game.specs.map.planets.find( _.coords == sourceShip.coords).foreach { p =>
      val planet = game.turnInfo(viewModel.turnShown).stateAfterCommands.planets.get(PlanetId(p.id))
      planet.fold(
        cargoTransferControl.setData(sourceShip, PlanetId(p.id))
      )(
        x => cargoTransferControl.setData(sourceShip, x)
      )
    }
    showTransferPopup()
  }

  def handleShipShipTransferButton(e: ActionEvent): Unit = {
    for(
      ship <- ship.value
    ) {
      val objectList = MapObject.findAtCoords(game, viewModel.turnShown)(ship.coords).filter(mo =>
        mo match {
          case MapObject.OwnShip(_, _, _, s) => s.id != ship.id
          case MapObject.Target(_, _, _, _) => true
          case _ => false
        }
      )
      if(objectList.size > 1) {
        targetSelectionControl.items = ObservableBuffer(objectList)
        val point = gpCargo.localToScreen(0, 0)
        transferTargetSelectionPopup.show(gpCargo.getScene.getWindow, point.getX, point.getY)
      } else if(objectList.size == 1) {
        onTransferDestinationSelected(objectList.head)
      }
    }
  }

  def onTransferDestinationSelected(mo: MapObject) = {
    mo match {
      case MapObject.OwnShip(id, _, _, _) =>
        val otherShip = game.turnInfo(viewModel.turnShown).stateAfterCommands.ownShips(ShipId(id))
        cargoTransferControl.setData(ship.value.get, otherShip)
      case MapObject.Planet(id, _, _) =>
        val planet = game.turnInfo(viewModel.turnShown).stateAfterCommands.planets(PlanetId(id))
        cargoTransferControl.setData(ship.value.get, planet)
      case MapObject.Target(id, _, _, _) =>
        val target = game.turnInfo(viewModel.turnShown).stateAfterCommands.ships(ShipId(id)).asInstanceOf[Target]
        cargoTransferControl.setData(ship.value.get, target.id)
    }
    showTransferPopup()
  }

  def hidePopup(): Unit = {
    transferPopup.hide()
  }
}