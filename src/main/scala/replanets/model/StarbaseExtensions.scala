package replanets.model

import replanets.common.{Constants, Cost, Minerals}
import replanets.common.NumberExtensions._

object StarbaseExtensions {
  implicit class StarbaseExt(val starbase: Starbase) extends AnyVal {

    def maxPossibleDefencesBuild() = {
      val planet = starbase.planet
      CalcUtils.maxPossibleUnitsForGivenCost(planet.surfaceMinerals, planet.money, planet.supplies, Constants.DefenceCost)
        .upperBound(Constants.MaxBaseDefences - starbase.defences)
    }

    def maxPossibleFightersBuild() = {
      val planet = starbase.planet
      CalcUtils.maxPossibleUnitsForGivenCost(planet.surfaceMinerals, planet.money, planet.supplies, Constants.FighterCost)
        .upperBound(Constants.MaxBaseFighters - starbase.fightersNumber)
    }
  }
}
