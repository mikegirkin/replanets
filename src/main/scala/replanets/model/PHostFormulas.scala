package replanets.model

import replanets.common.{EngspecItem, OneBasedIndex}

object PHostFormulas extends Formulas {
  override def maxFactories(colonistClans: Int): Int = ???

  override def fuelBurn(engSpecs: IndexedSeq[EngspecItem], engineId: OneBasedIndex, warp: Int, mass: Int, dx: Int, dy: Int, isGravitonic: Boolean): Int = ???

  override def maxMines(colonistClans: Int): Int = ???

  override def miningRate(density: Int, mines: Int, raceId: Int, nativeId: Int): Int = ???

  override def maxDefences(colonistClans: Int): Int = ???
}
