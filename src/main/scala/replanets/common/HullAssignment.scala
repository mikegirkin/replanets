package replanets.common

import java.nio.file.Path

import replanets.recipes.WORD

case class HullAssignment(
  availableHulls: IndexedSeq[IndexedSeq[Short]]
) {
  def getRaceHullIds(raceId: RaceId): IndexedSeq[Short] = {
    availableHulls(raceId.value - 1)
  }
}

object HullAssignment {
  val numberOfHullsPerRace = 20

  def fromFile(file: Path): HullAssignment = {
    val assignments = WORD.readFromFile(file, Constants.NumberOfRaces * numberOfHullsPerRace)
      .grouped(numberOfHullsPerRace)
      .take(Constants.NumberOfRaces)
      .map { raceArray =>
        raceArray.filter(_ != 0).toIndexedSeq
      }
      .toIndexedSeq

    HullAssignment(assignments)
  }
}