package replanets.model.commands

import replanets.common.{PlanetId, Planet, ServerData}
import replanets.model.Specs
import replanets.common.NumberExtensions._

case class BuildDefences(
  objectId: PlanetId,
  number: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BuildDefences(otherPlanetId, _) if otherPlanetId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    number > 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(objectId)
    val toBuild = number.bounded(0, maxPossible(planet, specs))
    val (suppliesRemaining, moneyRemaining) =
      specs.formulas.remainingResourcesAfterStructuresBuilt(11)(toBuild, planet.supplies, planet.money)

    if(toBuild == 0) state
    else {
      state.copy(
        planets = state.planets.updated(objectId, planet.copy(
          defencesNumber = planet.defencesNumber + toBuild,
          money = moneyRemaining,
          supplies = suppliesRemaining
        ))
      )
    }
  }

  private def maxPossible(planet: Planet, specs: Specs) = {
    Math.min(
      specs.formulas.maxDefences(planet.colonistClans) - planet.defencesNumber,
      specs.formulas.maxDefencesForMoney(planet.supplies, planet.money)
    )
  }
}