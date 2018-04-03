package replanets.model.commands.v0

import replanets.common.{Fcode, GameState, PlanetId}
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

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    val changed = for(
      planet <- initial.planets.get(objectId)
    ) yield planet.fcode.value != newFcode.value
    changed.getOrElse(false)
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    state.copy(
      planets = state.planets.updated(objectId, state.planets(objectId).copy(fcode = newFcode))
    )
  }
}
