package replanets.model.commands.v0

import replanets.common.{GameState, ShipId}
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

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    initial.ownShips(shipId).name != name
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    val shipState = state.ownShips(shipId).copy(
      name = name
    )
    state.copy(ships = state.ships.updated(shipId, shipState))
  }
}
