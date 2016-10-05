package replanets.model.commands

import replanets.common.{Constants, PlanetId, ServerData}
import replanets.model.Specs

import replanets.model.StarbaseExtensions._
import replanets.common.NumberExtensions._

case class BaseBuildDefences(
  baseId: PlanetId,
  defencesToBuild: Int
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BaseBuildDefences(otherBaseId, _) => otherBaseId == baseId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    defencesToBuild != 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(baseId)
    val base = state.bases(baseId)
    val actualBuild = base.maxPossibleDefencesBuild().upperBound(defencesToBuild)
    val totalCost = Constants.DefenceCost.mul(actualBuild)
    val (remainingMoney, remainingSupplies) =
      if(planet.money < totalCost.money) (0, planet.supplies - (totalCost.money - planet.money))
      else (planet.money - totalCost.money, planet.supplies)
    val newPlanetState = planet.copy(
      surfaceMinerals = planet.surfaceMinerals.minus(totalCost),
      money = remainingMoney,
      supplies = remainingSupplies
    )
    val newBaseState = base.copy(
      defences = base.defences + actualBuild,
      planet = newPlanetState
    )
    state.copy(
      bases = state.bases.updated(baseId, newBaseState),
      planets = state.planets.updated(baseId, newPlanetState)
    )
  }

  override def mergeWith(newerCommand: PlayerCommand): PlayerCommand = {
    newerCommand match {
      case BaseBuildDefences(newerBaseId, newerDelta) if newerBaseId == baseId => this.copy(defencesToBuild = this.defencesToBuild + newerDelta)
      case _ => this
    }
  }

}
