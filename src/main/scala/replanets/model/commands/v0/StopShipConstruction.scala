package replanets.model.commands.v0

import replanets.common.{GameState, PlanetId}
import replanets.model.Specs

case class StopShipConstruction(
  objectId: PlanetId
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case StartShipConstruction(baseId, _, _, _, _, _, _) if baseId == objectId => true
      case StopShipConstruction(baseId) if baseId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    val base = initial.bases(objectId)
    if(base.shipBeingBuilt.isDefined) true else false
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    val base = state.bases(objectId)
    state.copy(
      bases = state.bases.updated(objectId, base.copy(
        shipBeingBuilt = None
      ))
    )
  }
}