package replanets.model.commands

import replanets.common._
import replanets.model.{Cargo, Specs}

case class ShipToOtherPlanetTransfer(
  shipId: ShipId,
  planetId: PlanetId,
  transfer: Cargo
)  extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case ShipToOtherPlanetTransfer(newObjectId, _, _) => this.shipId == newObjectId
      case _ => false
    }
  }
  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    transfer != Cargo.zero
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val ship = state.ownShips(shipId)
    val newShipState = ship.copy(
      minerals = ship.minerals.minus(transfer.minerals),
      supplies = ship.supplies - transfer.supplies,
      colonistClans = ship.colonistClans - transfer.colonists,
      money = ship.money - transfer.money,
      torpsFightersLoaded = ship.torpsFightersLoaded - transfer.torps,
      transferToPlanet = Some(
        TransferToPlanet(
          transfer.neu,
          transfer.tri,
          transfer.dur,
          transfer.mol,
          transfer.colonists,
          transfer.supplies,
          planetId
        )
      )
    )
    state.copy(
      ships = state.ships.updated(newShipState.id, newShipState)
    )
  }

  override def mergeWith(newerCommand: PlayerCommand): PlayerCommand = {
    newerCommand match {
      case ShipToOtherPlanetTransfer(otherShip, otherPlanetId, otherTransfer) if shipId == otherShip && planetId == otherPlanetId =>
        this.copy(
          transfer = transfer.plus(otherTransfer)
        )
      case _ => this
    }
  }
}
