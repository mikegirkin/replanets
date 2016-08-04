package replanets.common

import java.nio.file.Path

import replanets.recipes.WORD

case class HullAssignment(
  availableHulls: IndexedSeq[IndexedSeq[Short]]
)

object HullAssignment {
  val numberOfHullsPerRace = 20

  def fromFile(file: Path): HullAssignment = {
    val assignments = WORD.readFromFile(file, Constants.NumberOfRaces * numberOfHullsPerRace)
      .grouped(numberOfHullsPerRace)
      .take(Constants.NumberOfRaces)
      .map { raceArray =>
        raceArray.filter(_ != 0).map(x => (x - 1).toShort).toIndexedSeq
      }
      .toIndexedSeq

    HullAssignment(assignments)
  }
}