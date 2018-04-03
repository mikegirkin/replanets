package replanets.model.commands.v0

import replanets.common.{Constants, GameState, PlanetId}
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

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    initial.planets(planetId).buildBase == 0
  }

  override def apply(state: GameState, specs: Specs): GameState = {
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