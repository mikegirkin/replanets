package replanets.model.commands

import replanets.common.{Constants, PlanetId, ServerData, ShipId}
import replanets.model.Specs

case class SetMission(
  shipId: ShipId,
  missionId: Int,
  towArgument: Int = 0,
  interceptArgument: Int = 0
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetMission(otherShipId, _, _, _) => this.shipId == otherShipId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    initial.ownShips(shipId).missionId != missionId
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val shipState = state.ownShips(shipId).copy(
      missionId = this.missionId,
      interceptTargetId = this.interceptArgument,
      towShipId = this.towArgument
    )
    state.copy(ships = state.ships.updated(shipId, shipState))
  }
}
