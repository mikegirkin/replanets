package replanets.model

import replanets.common.{Constants, Cost, Minerals}
import replanets.common.NumberExtensions._

object StarbaseExtensions {
  implicit class StarbaseExt(val starbase: Starbase) extends AnyVal {
    private def saturatedDiv(a: Int, b: Int, saturator: Int = Int.MaxValue): Int =
      if(b == 0) saturator
      else a / b

    private def maxPossibleUnitsForGivenCost(minerals: Minerals, money: Int, supplies: Int, cost: Cost) = {
      Seq(
        saturatedDiv(minerals.tritanium, cost.tri),
        saturatedDiv(minerals.duranium, cost.dur),
        saturatedDiv(minerals.molybdenium, cost.mol),
        saturatedDiv(money + supplies, cost.money)
      ).min
    }

    def maxPossibleDefencesBuild() = {
      val planet = starbase.planet
      maxPossibleUnitsForGivenCost(planet.surfaceMinerals, planet.money, planet.supplies, Constants.DefenceCost)
        .upperBound(Constants.MaxBaseDefences - starbase.defences)
    }

    def maxPossibleFightersBuild() = {
      val planet = starbase.planet
      maxPossibleUnitsForGivenCost(planet.surfaceMinerals, planet.money, planet.supplies, Constants.FighterCost)
        .upperBound(Constants.MaxBaseFighters - starbase.fightersNumber)
    }
  }
}
