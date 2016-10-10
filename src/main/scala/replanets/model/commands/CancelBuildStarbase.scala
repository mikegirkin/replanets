package replanets.model.commands

import replanets.common.{PlanetId, ServerData}
import replanets.model.Specs

case class CancelBuildStarbase(
  planetId: PlanetId
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BuildStarbase(otherPlanetId) => otherPlanetId == planetId
      case CancelBuildStarbase(otherPlanetId) => otherPlanetId == planetId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    initial.planets(planetId).buildBase != 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planetState = state.planets(planetId).copy(buildBase = 0)
    state.copy(
      planets = state.planets.updated(planetId, planetState)
    )
  }
}
