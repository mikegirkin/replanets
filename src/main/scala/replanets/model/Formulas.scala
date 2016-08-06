package replanets.model

/**
  * Created by mgirkin on 27/07/2016.
  */

trait Formulas {
  def maxFactories(colonistClans: Int):Int
  def maxMines(colonistClans: Int): Int
  def maxDefences(colonistClans: Int): Int
  def miningRate(density: Int, mines: Int, raceId: Int, nativeId: Int): Int
}
