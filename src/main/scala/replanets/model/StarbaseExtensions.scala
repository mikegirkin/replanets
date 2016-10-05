package replanets.model

import replanets.common.Constants

object StarbaseExtensions {
  implicit class StarbaseExt(val starbase: Starbase) extends AnyVal {
    def maxPossibleDefencesBuild() = {
      val planet = starbase.planet
      Seq(
        if(Constants.DefenceCost.tri > 0) planet.surfaceMinerals.tritanium / Constants.DefenceCost.tri else Int.MaxValue,
        if(Constants.DefenceCost.dur > 0) planet.surfaceMinerals.duranium / Constants.DefenceCost.dur else Int.MaxValue,
        if(Constants.DefenceCost.mol > 0) planet.surfaceMinerals.molybdenium / Constants.DefenceCost.mol else Int.MaxValue,
        if(Constants.DefenceCost.money > 0) (planet.money + planet.supplies) / Constants.DefenceCost.money else Int.MaxValue,
        Constants.MaxBaseDefences - starbase.defences
      ).min
    }
  }
}
