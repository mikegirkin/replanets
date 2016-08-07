package replanets.ui

import replanets.model.{Formulas, Game}

import scalafx.scene.control.Label
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

/**
  * Created by mgirkin on 07/08/2016.
  */
trait IShipDetailsView {
  def rootPane: Pane
  def setData(shipId: Int): Unit
}

@sfxml
class ShipDetailsView(
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
) extends IShipDetailsView {

  def setData(shipId: Int) = {
    lblShipId.text = shipId.toString
    val shipRecord = game.turnSeverData(viewModel.turnShown).ships.find(_.shipId == shipId)
    shipRecord.foreach { ship =>
      val hull = game.specs.getHull(ship.ownerId - 1, ship.hullTypeId)
      lblShipOwningRace.text = game.races(ship.ownerId - 1).shortname
      lblShipName.text = ship.name
      lblHullName.text = hull.name
      lblShipCoords.text = s"(${ship.xPosition}, ${ship.yPosition})"
      lblDestination.text = s"(${ship.xPosition + ship.xDistanceToWaypoint}, ${ship.yPosition + ship.yDistanceToWaypoint})"
      lblWarp.text = ship.warp.toString
      lblFuel.text = s"${ship.neutronium} / ${hull.fuelTankSize}"
      val fuelBurn = game.formulas.fuelBurn(
        game.specs.engineSpecs, ship.engineTypeId, ship.warp, ship.loadedMass,
        ship.xDistanceToWaypoint, ship.yDistanceToWaypoint, game.specs.isGravitonic(ship.hullTypeId))
      lblBurn.text = fuelBurn.toString //TODO: Formula for fuel burning
      lblMass.text = s"${hull.mass + ship.loadedMass}"
      lblFcode.text = ship.fcode
      lblCrew.text = s"${ship.crew} / ${hull.crewSize}"
      lblDamage.text = s"${ship.damage} %"
      lblMission.text = s"${ship.mission}" //TODO: Ship mission maps
      lblEnemy.text = if(ship.primaryEnemy != 0)  game.races(ship.primaryEnemy - 1).shortname else "(none)"
      lblEquipEngines.text = game.specs.engineSpecs(ship.engineTypeId).name
      lblEquipBeams.text = s"${ship.numberOfBeams} - ${game.specs.beamSpecs(ship.beamType).name}"
      lblEquipLaunchers.text = s"${ship.numberOfTorpLaunchers} - ${game.specs.torpSpecs(ship.torpsType).name}"
      lblCargo.text = ship.loadedMass.toString
      lblNeu.text = ship.neutronium.toString
      lblTri.text = ship.tritanium.toString
      lblDur.text = ship.duranium.toString
      lblMol.text = ship.molybdenium.toString
      lblSupplies.text = ship.supplies.toString
      lblClans.text = ship.colonistClans.toString
      lblMoney.text = ship.money.toString
      lblTorps.text = ship.torpsFightersLoaded.toString
    }
  }
}
