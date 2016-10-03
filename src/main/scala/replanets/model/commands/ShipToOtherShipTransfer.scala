package replanets.model.commands

import replanets.common.{RaceId, ServerData, ShipId, TransferToEnemyShip}
import replanets.model.{Cargo, Specs}

case class ShipToOtherShipTransfer(
  sourceId: ShipId,
  targetId: ShipId,
  transfer: Cargo
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case ShipToOtherShipTransfer(otherSource, _, _) => otherSource == sourceId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    transfer != Cargo.zero
  }


  override def apply(state: ServerData, specs: Specs): ServerData = {
    val sourceShip = state.ownShips(sourceId)
    val sourceShipState = sourceShip.copy(
      minerals = sourceShip.minerals.minus(transfer.minerals),
      supplies = sourceShip.supplies - transfer.supplies,
      colonistClans = sourceShip.colonistClans - transfer.colonists,
      money = sourceShip.money - transfer.money,
      torpsFightersLoaded = sourceShip.torpsFightersLoaded - transfer.torps,
      transferToEnemyShip = Some(
        TransferToEnemyShip(
          transfer.neu,
          transfer.tri,
          transfer.dur,
          transfer.mol,
          transfer.supplies,
          transfer.colonists,
          targetId
        )
      )
    )

    state.copy(ships = state.ships.updated(sourceId, sourceShipState))
  }

  override def mergeWith(newerCommand: PlayerCommand): PlayerCommand = {
    newerCommand match {
      case ShipToOtherShipTransfer(otherSourceId, otherTargetId, otherTransfer) =>
        if(this.targetId == otherTargetId)
          this.copy(transfer = transfer.plus(otherTransfer))
        else
          newerCommand
      case _ => this
    }
  }
}
