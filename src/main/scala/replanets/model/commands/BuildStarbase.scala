package replanets.model.commands

import replanets.common.{Constants, PlanetId, ServerData}
import replanets.model.{MoneySupplies, Specs}

case class BuildStarbase(
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
    initial.planets(planetId).buildBase == 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(planetId)
    val remaining = specs.formulas.remainingMoneySupplies(MoneySupplies(planet.money, planet.supplies), Constants.StarbaseCost.money)
    val newPlanetState = planet.copy(
      buildBase = 1,
      surfaceMinerals = planet.surfaceMinerals.minus(Constants.StarbaseCost),
      money = remaining.money,
      supplies = remaining.supplies
    )
    state.copy(
      planets = state.planets.updated(planetId, newPlanetState)
    )
  }
}