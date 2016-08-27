package replanets.model.commands

import replanets.common.{PlanetId, ServerData}
import replanets.common.NumberExtensions._
import replanets.model.Specs

case class SetColonistTax(
  planetId: PlanetId,
  newTax: Int
) extends PlayerCommand {

  override def objectId = planetId

  override def isReplacableBy(other: PlayerCommand): Boolean = other match {
    case SetColonistTax(otherPlanetId, _) if otherPlanetId == planetId => true
    case _ => false
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    if(initial.planets(planetId).colonistTax == newTax) false
    else true
  }

  def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(planetId)
    state.copy(
      planets = state.planets.updated(planetId, planet.copy(
        colonistTax = newTax.bounded(0, 100).toShort
      ))
    )
  }
}