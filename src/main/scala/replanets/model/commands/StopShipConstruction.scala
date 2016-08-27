package replanets.model.commands

import replanets.common.{PlanetId, ServerData}
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

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    val base = initial.bases(objectId)
    if(base.shipBeingBuilt.isDefined) true else false
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val base = state.bases(objectId)
    state.copy(
      bases = state.bases.updated(objectId, base.copy(
        shipBeingBuilt = None
      ))
    )
  }
}