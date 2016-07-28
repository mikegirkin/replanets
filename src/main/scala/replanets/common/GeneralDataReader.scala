package replanets.common

import replanets.recipes._

case class RaceScore(
  planets: Short,
  capitalShips: Short,
  freighters: Short,
  starbases: Short
) {
  def score: Int = 10 * planets + 10 * capitalShips + 1 * freighters + 120 * starbases
}

case class ScoreInfo(
  scoreInfo: IndexedSeq[RaceScore]
)

case class GeneralTurnInformation(
  timestamp: String,
  scoreInfo: ScoreInfo,
  playerId: Short,
  passwordField: IndexedSeq[Byte],
  shipsChecksum: Int,
  planetsChecksum: Int,
  basesChecksum: Int,
  turnNumber: Short,
  timestampChecksum: Short
)


object GeneralDataReader {
  def read(section: Iterable[Byte]): GeneralTurnInformation = {
    val it = section.iterator
    GeneralTurnInformation(
      SpacePaddedString(18).read(it),
      ScoreInfo(
        ArrayRecipe(11, RecordRecipe(8, int => RaceScore(
          WORD.read(int),
          WORD.read(int),
          WORD.read(int),
          WORD.read(int)
        ))).read(it)
      ),
      WORD.read(it),
      ArrayRecipe(20, BYTE).read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      WORD.read(it),
      WORD.read(it)
    )
  }
}
