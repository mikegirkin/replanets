package replanets.model.commands

import replanets.common.{PlanetId, PlanetRecord, ServerData}
import replanets.model.Specs
import replanets.common.NumberExtensions._

case class BuildFactories(
  objectId: PlanetId,
  number: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BuildFactories(planetId, _) if planetId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    val planet = initial.planets(objectId)
    maxPossible(planet, specs) > 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(objectId)
    val toBuild = number.bounded(0, maxPossible(planet, specs))
    val moneyRemaining = (planet.money - toBuild * 3).lowerBound(0)
    val costUnpaid = toBuild + (toBuild * 3 - planet.money).lowerBound(0)
    val suppliesRemaining = planet.supplies - costUnpaid

    if(toBuild == 0) state
    else {
      state.copy(
        planets = state.planets.updated(objectId, planet.copy(
          factoriesNumber = planet.factoriesNumber + toBuild,
          money = moneyRemaining,
          supplies = suppliesRemaining
        ))
      )
    }
  }

  def maxPossible(planet: PlanetRecord, specs: Specs) =
    Math.min(
      specs.formulas.maxFactories(planet.colonistClans) - planet.factoriesNumber,
      specs.formulas.maxFactoriesForMoney(planet.supplies, planet.money)
    )
}
