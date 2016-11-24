package replanets.model.commands

import replanets.common.{Constants, PlanetId, ServerData}
import replanets.model.{MoneySupplies, Specs}
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
    val actualBuild = defencesToBuild.upperBound(base.maxPossibleDefencesBuild())
    val totalCost = Constants.DefenceCost.mul(actualBuild)
    val remaining = specs.formulas.remainingMoneySupplies(
      MoneySupplies(planet.money, planet.supplies),
      totalCost.money
    )
    val newPlanetState = planet.copy(
      surfaceMinerals = planet.surfaceMinerals.minus(totalCost),
      money = remaining.money,
      supplies = remaining.supplies
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
