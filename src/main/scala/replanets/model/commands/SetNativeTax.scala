package replanets.model.commands

import replanets.common.{OneBasedIndex, PlanetId, ServerData}
import replanets.common.NumberExtensions._
import replanets.model.Specs

case class SetNativeTax(
  planetId: PlanetId,
  newTax: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = other match {
    case SetNativeTax(otherPlanetId, _) if otherPlanetId == planetId => true
    case _ => false
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    if(initial.planets(planetId).nativeTax == newTax) false
    else true
  }

  def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(planetId)
    state.copy(
      planets = state.planets.updated(planetId, planet.copy(
        nativeTax = newTax.bounded(0, 100).toShort
      ))
    )
  }
}
