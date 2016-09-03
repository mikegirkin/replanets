package replanets.model.commands

import replanets.common.{PlanetId, ServerData, ShipId}
import replanets.model.{Cargo, Specs}

case class ChangeBasePrimaryOrder(
  objectId: PlanetId,
  orderId: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case ChangeBasePrimaryOrder(otherPlanetId, _) if otherPlanetId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    initial.bases.get(objectId).exists { sb =>
      sb.primaryOrder != orderId
    }
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    state.bases.get(objectId).map { sb =>
      state.copy(
        bases = state.bases.updated(objectId, sb.copy(
          primaryOrder = orderId
        ))
      )
    }.getOrElse(state)
  }
}