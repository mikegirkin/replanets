package replanets.model.commands

import replanets.common.{ServerData, ShipId}
import replanets.model.Specs
import replanets.common.NumberExtensions._

case class SetShipWarp(
  objectId: ShipId,
  newWarp: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetShipWarp(shipId, warp) if shipId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    initial.ownShips.get(objectId).exists { _.warp != newWarp }
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    state.ownShips.get(objectId).map { s =>
      state.copy(
        ships = state.ships.updated(objectId, s.copy(
          warp = newWarp.bounded(0, 9)
        ))
      )
    }.getOrElse(state)
  }
}
