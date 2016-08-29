package replanets.model.commands

import replanets.common.{PlanetId, Planet, ServerData}
import replanets.model.Specs
import replanets.common.NumberExtensions._

case class BuildMines(
  objectId: PlanetId,
  number: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BuildMines(otherPlanetId, _) if otherPlanetId == objectId => true
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    val planet = initial.planets(objectId)
    number > 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(objectId)
    val toBuild = number.bounded(0, maxMinesPossible(planet, specs))
    val (suppliesRemaining, moneyRemaining) =
      specs.formulas.remainingResourcesAfterStructuresBuilt(5)(toBuild, planet.supplies, planet.money)

    if(toBuild == 0) state
    else {
      state.copy(
        planets = state.planets.updated(objectId, planet.copy(
          minesNumber = planet.minesNumber + toBuild,
          money = moneyRemaining,
          supplies = suppliesRemaining
        ))
      )
    }
  }

  private def maxMinesPossible(planet: Planet, specs: Specs) = {
    Math.min(
      specs.formulas.maxMines(planet.colonistClans) - planet.minesNumber,
      specs.formulas.maxMinesForMoney(planet.supplies, planet.money)
    )
  }
}
