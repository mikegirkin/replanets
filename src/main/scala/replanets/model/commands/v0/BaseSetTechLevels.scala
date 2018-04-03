package replanets.model.commands.v0

import replanets.common.{GameState, PlanetId}
import replanets.model.Specs

case class BaseSetTechLevels(
  baseId: PlanetId,
  hullTechlevel: Int,
  engineTechLevel: Int,
  beamTechLevel: Int,
  torpTechLevel: Int
) extends PlayerCommand {

  override def isReplacableBy(other: PlayerCommand): Boolean = {
    other match {
      case BaseSetTechLevels(otherPlanetId, _, _, _, _) => otherPlanetId == baseId
      case _ => false
    }
  }

  override def isAddDiffToInitialState(initial: GameState, specs: Specs): Boolean = {
    val base = initial.bases(baseId)
    base.hullsTech != hullTechlevel ||
    base.engineTech != engineTechLevel ||
    base.beamTech != beamTechLevel ||
    base.torpedoTech != torpTechLevel
  }

  override def apply(state: GameState, specs: Specs): GameState = {
    val base = state.bases(baseId)
    val planet = state.planets(baseId)
    val upgradeCost = specs.formulas.techUpgradeCost(base.hullsTech, hullTechlevel) +
      specs.formulas.techUpgradeCost(base.engineTech, engineTechLevel) +
      specs.formulas.techUpgradeCost(base.beamTech, beamTechLevel) +
      specs.formulas.techUpgradeCost(base.torpedoTech, torpTechLevel)
    val (remainingMoney, remainingSupplies) =
      if(planet.money < upgradeCost) (0, planet.supplies - (upgradeCost - planet.money))
      else (planet.money - upgradeCost, planet.supplies)

    val newPlanetState = planet.copy(
      money = remainingMoney,
      supplies = remainingSupplies
    )
    val newBaseState = base.copy(
      hullsTech = Math.max(hullTechlevel, base.hullsTech),
      engineTech = Math.max(engineTechLevel, base.engineTech),
      beamTech = Math.max(beamTechLevel, base.beamTech),
      torpedoTech = Math.max(torpTechLevel, base.torpedoTech)
    )
    state.copy(
      planets = state.planets.updated(baseId, newPlanetState),
      bases = state.bases.updated(baseId, newBaseState)
    )
  }

}
