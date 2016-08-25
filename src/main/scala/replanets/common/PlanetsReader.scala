package replanets.common

import replanets.recipes._

case class PlanetRecord(
  ownerId: RaceId,
  id: PlanetId,
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
  colonistTax: Int,
  nativeTax: Short,
  colonistHappiness: Short,
  nativeHappiness: Short,
  nativeGovernment: NativeGovernment,
  nativeClans: Int,
  nativeRace: NativeRace,
  temperature: Short,
  buildBase: Short
)

object PlanetsReader {
  val planetRecipe = RecordRecipe(
    it => PlanetRecord(
      RaceId(WORD.read(it)),
      PlanetId(WORD.read(it)),
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
      Constants.nativeGovernments(Id(WORD.read(it))),
      DWORD.read(it),
      Constants.natives(Id(WORD.read(it))),
      WORD.read(it),
      WORD.read(it)
    )
  )

  def read(it: Iterator[Byte]): Map[PlanetId, PlanetRecord] = {
    val recordsNum = WORD.read(it)
    ArrayRecipe(recordsNum, planetRecipe).read(it).map { x => x.id -> x }.toMap
  }
}