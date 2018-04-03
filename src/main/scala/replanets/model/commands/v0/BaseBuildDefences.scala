package replanets.model.commands.v0

import replanets.common.NumberExtensions._
import replanets.common.{Constants, GameState, PlanetId}
import replanets.model.StarbaseExtensions._
import replanets.model.{MoneySupplies, Specs}

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

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    defencesToBuild != 0
  }

  override def apply(state: GameState, specs: Specs): GameState = {
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
