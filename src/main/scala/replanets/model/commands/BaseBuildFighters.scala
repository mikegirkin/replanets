package replanets.model.commands

import replanets.common.{Constants, PlanetId, ServerData}
import replanets.model.Specs
import replanets.common.NumberExtensions._
import replanets.model.StarbaseExtensions._

case class BaseBuildFighters(
  baseId: PlanetId,
  fightersToBuild: Int
) extends PlayerCommand {
  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BaseBuildFighters(otherBaseId, _) => otherBaseId == baseId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: ServerData, specs: Specs): Boolean = {
    fightersToBuild != 0
  }

  override def apply(state: ServerData, specs: Specs): ServerData = {
    val planet = state.planets(baseId)
    val base = state.bases(baseId)
    val actualBuild = fightersToBuild.upperBound(base.maxPossibleFightersBuild())
    val totalCost = Constants.FighterCost.mul(actualBuild)
    val (remainingMoney, remainingSupplies) =
      if(planet.money < totalCost.money) (0, planet.supplies - (totalCost.money - planet.money))
      else (planet.money - totalCost.money, planet.supplies)
    val newPlanetState = planet.copy(
      surfaceMinerals = planet.surfaceMinerals.minus(totalCost),
      money = remainingMoney,
      supplies = remainingSupplies
    )
    val newBaseState = base.copy(
      fightersNumber = base.fightersNumber + actualBuild,
      planet = newPlanetState
    )
    state.copy(
      bases = state.bases.updated(baseId, newBaseState),
      planets = state.planets.updated(baseId, newPlanetState)
    )
  }

  override def mergeWith(newerCommand: PlayerCommand): PlayerCommand = {
    newerCommand match {
      case BaseBuildFighters(newerBaseId, newerDelta) if newerBaseId == baseId => this.copy(fightersToBuild = this.fightersToBuild + newerDelta)
      case _ => this
    }
  }
}
