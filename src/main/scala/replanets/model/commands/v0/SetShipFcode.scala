package replanets.model.commands.v0

import replanets.common.{Fcode, GameState, OwnShip, ShipId}
import replanets.model.Specs

case class SetShipFcode(
  objectId: ShipId,
  newFcode: Fcode
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetShipFcode(otherShipId, _) if objectId == otherShipId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    if(initial.ships(objectId).asInstanceOf[OwnShip].fcode != newFcode) true else false
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    val ship = state.ships(objectId)
    if(!ship.isInstanceOf[OwnShip]) state
    else {
      val ownShip = ship.asInstanceOf[OwnShip]
      state.copy(
        ships = state.ships.updated(objectId, ownShip.copy(fcode = newFcode))
      )
    }
  }
}