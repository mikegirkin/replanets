package replanets.common

import common.Constants
import replanets.recipes.WORD

case class HullAssignment(
  availableHulls: IndexedSeq[IndexedSeq[Short]]
)

object HullAssignment {
  val recipe = WORD
  val numberOfHullsPerRace = 20

  def readFromFile(filename: String) = {
    recipe.readFromFile(filename)
      .grouped(numberOfHullsPerRace)
      .take(Constants.numberOfRaces)
      .map { raceArray =>
        raceArray.filter(_ != 0).map(_ - 1).toIndexedSeq
      }
      .toIndexedSeq
  }
}