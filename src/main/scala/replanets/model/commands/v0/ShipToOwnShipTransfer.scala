package replanets.model.commands.v0

import replanets.common.{GameState, ShipId}
import replanets.model.{Cargo, Specs}

case class ShipToOwnShipTransfer(
  source: ShipId,
  target: ShipId,
  transfer: Cargo
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case ShipToOwnShipTransfer(otherSource, otherTarget, _) =>
        (otherSource == source && otherTarget == target) || (otherSource == target && otherTarget == source)
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    transfer != Cargo.zero
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    val sourceShip = state.ownShips(source)
    val targetShip = state.ownShips(target)
    val sourceShipState = sourceShip.copy(
      minerals = sourceShip.minerals.minus(transfer.minerals),
      supplies = sourceShip.supplies - transfer.supplies,
      colonistClans = sourceShip.colonistClans - transfer.colonists,
      money = sourceShip.money - transfer.money,
      torpsFightersLoaded = sourceShip.torpsFightersLoaded - transfer.torps
    )
    val targetShipState = targetShip.copy(
      minerals = targetShip.minerals.plus(transfer.minerals),
      supplies = targetShip.supplies + transfer.supplies,
      colonistClans = targetShip.colonistClans + transfer.colonists,
      money = targetShip.money + transfer.money,
      torpsFightersLoaded = targetShip.torpsFightersLoaded + transfer.torps
    )
    state.copy(ships = state.ships.updated(source, sourceShipState).updated(target, targetShipState))
  }

  override def mergeWith(newerCommand: PlayerCommand): PlayerCommand = {
    newerCommand match {
      case ShipToOwnShipTransfer(otherSourceId, otherTargetId, otherTransfer) =>
        if(this.source == otherSourceId && this.target == otherTargetId)
          this.copy(
            transfer = transfer.plus(otherTransfer)
          )
        else if(this.target == otherSourceId && this.source == otherTargetId) {
          this.copy(
            transfer = transfer.minus(otherTransfer)
          )
        }
        else this
      case _ => this
    }
  }
}
