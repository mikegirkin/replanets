package replanets.model

import replanets.common.{EngspecItem, HullspecItem}

object PHostFormulas extends Formulas {

  def unitsInMinefieldByRadius(radius: Int): Int = ???

  override def minefieldUnitsNextTurn(units: Int): Int = ???

  override def mineHitDamage(hull: HullspecItem): Int = ???

  override def webHitDamage(hull: HullspecItem): Int = ???

  override def maxFactories(colonistClans: Int): Int = ???

  override def fuelBurn(engine: EngspecItem, warp: Int, mass: Int, dx: Int, dy: Int, isGravitonic: Boolean): Int = ???

  override def maxMines(colonistClans: Int): Int = ???

  override def miningRate(density: Int, mines: Int, raceId: Int, nativeId: Int): Int = ???

  override def maxDefences(colonistClans: Int): Int = ???
}
