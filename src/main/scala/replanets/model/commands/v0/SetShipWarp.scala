package replanets.model.commands.v0

import replanets.common.NumberExtensions._
import replanets.common.{GameState, ShipId}
import replanets.model.Specs

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

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    initial.ownShips.get(objectId).exists { _.warp != newWarp }
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    state.ownShips.get(objectId).map { s =>
      state.copy(
        ships = state.ships.updated(objectId, s.copy(
          warp = newWarp.bounded(0, 9)
        ))
      )
    }.getOrElse(state)
  }
}
