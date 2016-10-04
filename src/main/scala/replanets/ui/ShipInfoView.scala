package replanets.ui

import replanets.common._
import replanets.model.{CargoHold, Game}
import replanets.ui.actions.Actions
import replanets.ui.controls._
import replanets.ui.viewmodels.ViewModel

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseButton, MouseEvent}
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
  val lblMissionAdditional: Label,
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
  val edShipName: TextField,
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

  val missionSelectPopup = new SelectMissionView(
    () => ship.value.get,
    game.turnInfo(viewModel.turnShown).stateAfterCommands,
    game.specs.missions,
    missionId => setCurrentShipMission(missionId)
  )

  val enemySelectPopup = new SelectEnemyView(
    game.races,
    raceId => setPrimaryEnemy(raceId)
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
    val state = game.turnInfo(viewModel.turnShown).stateAfterCommands
    val newShip = state.ships(shipId).asInstanceOf[OwnShip]
    ship.value = Some(newShip)

    lblShipId.text = newShip.id.value.toString
    lblShipOwningRace.text = if (newShip.owner.value > 0) {
      game.races(newShip.owner.value - 1).adjective
    } else {
      "Unknown"
    }
    lblShipName.text = newShip.name
    lblShipName.visible = true
    edShipName.text = newShip.name
    edShipName.visible = false
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
    lblMission.text = game.specs.missions.get(newShip.missionId)
    missionSelectPopup.setSelectedItem(newShip.missionId)
    lblMissionAdditional.text =
      if (newShip.missionId == Missions.TowMissionId && newShip.towShipId != 0) { //Tow
        s"Tow: ${state.ships(ShipId(newShip.towShipId)).name}"
      } else if(newShip.missionId == Missions.InterceptMissionId && newShip.interceptTargetId != 0) { //Intercept
        s"Intercepting ship id: ${newShip.interceptTargetId}"
      } else if(newShip.missionId >= 20 && game.specs.missions.argumentRequiremets.contains(newShip.missionId)) {
        s"I: ${newShip.interceptTargetId}  T: ${newShip.towShipId}"
      } else {
        ""
      }
    lblEnemy.text = newShip.primaryEnemy.fold(
      "None"
    )( raceId =>
      game.races(raceId.value - 1).shortname
    )
    enemySelectPopup.setSelected(newShip.primaryEnemy)
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

  def onChangeEnemyButton(e: ActionEvent) = {
    val point = lblEnemy.localToScreen(0, 0)
    enemySelectPopup.show(lblEnemy.getScene.getWindow, point.getX, point.getY)
  }

  def onChangeMissionButton(e: ActionEvent) = {
    missionSelectPopup.reset()
    val point = lblMission.localToScreen(0, 0)
    missionSelectPopup.show(lblMission.getScene.getWindow, point.getX, point.getY)
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

      val shipList = game.turnInfo(viewModel.turnShown)
        .stateAfterCommands
        .getShipsAtCoords(ship.coords).filter {
          _.id != ship.id
        }.map { s =>
          MapObject.forShip(s)
        }
      if(shipList.size > 1) {
        targetSelectionControl.items = ObservableBuffer(shipList)
        val point = gpCargo.localToScreen(0, 0)
        transferTargetSelectionPopup.show(gpCargo.getScene.getWindow, point.getX, point.getY)
      } else if(shipList.size == 1) {
        onTransferDestinationSelected(shipList.head)
      }
    }
  }

  def onTransferDestinationSelected(mo: MapObject) = {
    mo match {
      case MapObject.OwnShip(id, _, _) =>
        val otherShip = game.turnInfo(viewModel.turnShown).stateAfterCommands.ownShips(ShipId(id))
        cargoTransferControl.setData(ship.value.get, otherShip)
      case MapObject.Planet(id, _, _) =>
        val planet = game.turnInfo(viewModel.turnShown).stateAfterCommands.planets(PlanetId(id))
        cargoTransferControl.setData(ship.value.get, planet)
      case MapObject.Target(id, _, _) =>
        val target = game.turnInfo(viewModel.turnShown).stateAfterCommands.ships(ShipId(id)).asInstanceOf[Target]
        cargoTransferControl.setData(ship.value.get, target.id)
    }
    showTransferPopup()
  }

  def hideTransferPopup(): Unit = {
    transferPopup.hide()
  }

  def setCurrentShipMission(mission: SelectedMission): Unit = {
    ship.value.foreach(s =>
      actions.setMission(s, mission.missionId, mission.towArgument, mission.interceptArgument)
    )
    missionSelectPopup.hide()
  }

  def setPrimaryEnemy(raceId: Option[RaceId]): Unit = {
    ship.value.foreach(s =>
      actions.setPrimaryEnemy(s, raceId)
    )
    enemySelectPopup.hide()
  }

  def onShipNameClicked(e: MouseEvent): Unit = {
    if(e.button == MouseButton.Primary) {
      lblShipName.visible = false
      edShipName.visible = true
      edShipName.requestFocus()
    }
  }

  def onEdShipNameKeyPressed(e: KeyEvent): Unit = {
    if(e.code == KeyCode.Enter) {
      lblShipName.visible = true
      edShipName.visible = false
      ship.value.foreach { s =>
        actions.setShipName(s, edShipName.text.value)
      }
    }
  }
}