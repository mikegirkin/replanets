package replanets.model.commands.v0

import replanets.common.{GameState, PlanetId}
import replanets.model.Specs

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

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    initial.bases.get(objectId).exists { sb =>
      sb.primaryOrder != orderId
    }
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    state.bases.get(objectId).map { sb =>
      state.copy(
        bases = state.bases.updated(objectId, sb.copy(
          primaryOrder = orderId
        ))
      )
    }.getOrElse(state)
  }
}