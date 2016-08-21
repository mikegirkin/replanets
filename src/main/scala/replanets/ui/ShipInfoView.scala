package replanets.ui

import replanets.common.OwnShip
import replanets.model.Game
import replanets.ui.viewmodels.ViewModel

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 07/08/2016.
  */
trait IShipInfoView {
  def rootPane: Pane
  def setData(ship: OwnShip): Unit
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
  val lblWarp: Label,
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

  val game: Game,
  val viewModel: ViewModel
) extends IShipInfoView {

  def setData(ship: OwnShip) = {
    lblShipId.text = ship.id.value.toString
    lblShipOwningRace.text = game.races(ship.owner.value - 1).shortname
    lblShipName.text = ship.name
    lblHullName.text = ship.hull.name
    lblShipCoords.text = s"(${ship.x}, ${ship.y})"
    lblDestination.text = s"(${ship.x + ship.xDistanceToWaypoint}, ${ship.y + ship.yDistanceToWaypoint})"
    lblWarp.text = ship.warp.toString
    lblFuel.text = s"${ship.minerals.neutronium} / ${ship.hull.fuelTankSize}"
    val fuelBurn = game.formulas.fuelBurn(
      ship.engines, ship.warp, ship.fullMass,
      ship.xDistanceToWaypoint, ship.yDistanceToWaypoint, game.specs.isGravitonic(ship.hull.id))
    lblBurn.text = fuelBurn.toString
    lblMass.text = s"${ship.fullMass}"
    lblFcode.text = ship.fcode.value
    lblCrew.text = s"${ship.crew} / ${ship.hull.crewSize}"
    lblDamage.text = s"${ship.damage} %"
    lblMission.text = game.missions.get(ship.mission)
    lblEnemy.text = if(ship.primaryEnemy != 0)  game.races(ship.primaryEnemy - 1).shortname else "(none)"
    lblEquipEngines.text = ship.engines.name
    lblEquipBeams.text = ship.beams.map { b => s"${ship.numberOfBeams} - ${b.name}" }.getOrElse("")
    lblEquipLaunchers.text = ship.torpsType.map { tt => s"${ship.numberOfTorpLaunchers} - ${tt.name}" }.getOrElse("")
    lblCargo.text = ship.cargoMass.toString
    lblNeu.text = ship.minerals.neutronium.toString
    lblTri.text = ship.minerals.tritanium.toString
    lblDur.text = ship.minerals.duranium.toString
    lblMol.text = ship.minerals.molybdenium.toString
    lblSupplies.text = ship.supplies.toString
    lblClans.text = ship.colonistClans.toString
    lblMoney.text = ship.money.toString
    lblTorps.text = ship.torpsFightersLoaded.toString
  }
}
