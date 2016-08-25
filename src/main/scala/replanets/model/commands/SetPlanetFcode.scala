package replanets.model.commands

import replanets.common.{Fcode, PlanetId, ServerData}
import replanets.model.Specs

case class SetPlanetFcode(
  objectId: PlanetId,
  newFcode: Fcode
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case SetPlanetFcode(otherPlanetId, _) if objectId == otherPlanetId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData): Boolean = {
    val changed = for(
      planet <- initial.planets.get(objectId)
    ) yield planet.fcode.value != newFcode.value
    changed.getOrElse(false)
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    state.copy(
      planets = state.planets.updated(objectId, state.planets(objectId).copy(fcode = newFcode))
    )
  }
}
