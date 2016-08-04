package replanets.common

import replanets.recipes._

/**
  * Created by mgirkin on 26/07/2016.
  */

case class Minerals(
  neutronium: Int,
  tritanium: Int,
  duranium: Int,
  molybdenium: Int
)

case class PlanetRecord(
  ownerId: Short,
  planetId: Short,
  fcode: String,
  minesNumber: Short,
  factoriesNumber: Short,
  defencesNumber: Short,
  surfaceMinerals: Minerals,
  colonistClans: Int,
  supplies: Int,
  money: Int,
  coreMinerals: Minerals,
  densityMinerals: Minerals,
  colonistTax: Short,
  nativeTax: Short,
  colonistHappiness: Short,
  nativeHappiness: Short,
  nativeGovernment: Short,
  nativeClans: Int,
  nativeRace: Short,
  temperature: Short,
  buildBase: Short
)

object PlanetsReader {
  val planetRecordsFieldSize = 85

  val planetRecipe = RecordRecipe(
    planetRecordsFieldSize,
    it => PlanetRecord(
      WORD.read(it),
      WORD.read(it),
      SpacePaddedString(3).read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      Minerals(
        DWORD.read(it),
        DWORD.read(it),
        DWORD.read(it),
        DWORD.read(it)
      ),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      Minerals(
        DWORD.read(it),
        DWORD.read(it),
        DWORD.read(it),
        DWORD.read(it)
      ),
      Minerals(
        WORD.read(it),
        WORD.read(it),
        WORD.read(it),
        WORD.read(it)
      ),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      DWORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it)
    )
  )

  def read(it: Iterator[Byte]): IndexedSeq[PlanetRecord] = {
    val recordsNum = WORD.read(it)
    ArrayRecipe(recordsNum, planetRecipe).read(it)
  }
}