package replanets.common

import replanets.recipes._

case class Minerals(
  neutronium: Int,
  tritanium: Int,
  duranium: Int,
  molybdenium: Int
)

case class PlanetRecord(
  ownerId: Short,
  planetId: Short,
  fcode: Fcode,
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
  val planetRecipe = RecordRecipe(
    it => PlanetRecord(
      WORD.read(it),
      WORD.read(it),
      Fcode(FixedLengthString(3).read(it)),
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