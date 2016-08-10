package replanets.model

import replanets.common.{EngspecItem, OneBasedIndex}

/**
  * Created by mgirkin on 27/07/2016.
  */

trait Formulas {
  def maxFactories(colonistClans: Int):Int
  def maxMines(colonistClans: Int): Int
  def maxDefences(colonistClans: Int): Int
  def miningRate(density: Int, mines: Int, raceId: Int, nativeId: Int): Int
  def fuelBurn(engSpecs:IndexedSeq[EngspecItem], engineId: OneBasedIndex, warp: Int, mass: Int, dx: Int, dy: Int, isGravitonic: Boolean): Int
}
