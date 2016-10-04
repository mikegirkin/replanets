package replanets.model.commands

import replanets.common.{ServerData, ShipId}
import replanets.model.Specs

case class SetShipName(
  shipId: ShipId,
  name: String
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetShipName(otherShipId, _) => otherShipId == shipId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    initial.ownShips(shipId).name != name
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val shipState = state.ownShips(shipId).copy(
      name = name
    )
    state.copy(ships = state.ships.updated(shipId, shipState))
  }
}
