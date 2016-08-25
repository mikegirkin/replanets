package replanets.model.commands

import replanets.common.{Fcode, OwnShip, ServerData, ShipId}
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

  override def isAddDiffToInitialState(initial: ServerData): Boolean = {
    if(initial.ships(objectId).asInstanceOf[OwnShip].fcode != newFcode) true else false
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
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
