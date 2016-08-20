package replanets.model

import replanets.common.{Constants, EngspecItem, HullspecItem}

/**
  * Created by mgirkin on 27/07/2016.
  */

trait Formulas {
  def minefieldUnitsNextTurn(units: Int): Int
  def unitsInMinefieldByRadius(radius: Int): Int
  def mineHitDamage(hull: HullspecItem): Int
  def webHitDamage(hull: HullspecItem): Int
  def maxFactories(colonistClans: Int):Int
  def maxMines(colonistClans: Int): Int
  def maxDefences(colonistClans: Int): Int
  def miningRate(density: Int, mines: Int, raceId: Int, nativeId: Int): Int
  def fuelBurn(engine: EngspecItem, warp: Int, mass: Int, dx: Int, dy: Int, isGravitonic: Boolean): Int

  def techUpgradeCost(from: Int, to: Int) = {
    if(from >= to) 0
    else Constants.techLevelsCost.slice(from - 1, to - 1).sum
  }
}
