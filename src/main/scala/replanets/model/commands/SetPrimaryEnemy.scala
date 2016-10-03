package replanets.model.commands

import replanets.common.{RaceId, ServerData, ShipId}
import replanets.model.Specs

case class SetPrimaryEnemy(
  shipId: ShipId,
  enemyId: Option[RaceId]
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetPrimaryEnemy(othershipId, _) => this.shipId == othershipId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    initial.ownShips(shipId).primaryEnemy != enemyId
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val shipState = state.ownShips(shipId).copy(primaryEnemy = enemyId)
    state.copy(ships = state.ships.updated(shipId, shipState))
  }
}
