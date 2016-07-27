package replanets.common

import replanets.recipes.{DWORD, SpacePaddedString, WORD}

/**
  * Created by mgirkin on 26/07/2016.
  */
case class PlanetRecord(
  ownerId: Short,
  planetId: Short,
  fcode: String,
  minesNumber: Short,
  factoriesNumber: Short,
  defencesNumber: Short,
  neutroniumMined: Int,
  tritaniumMined: Int,
  duraniumMined: Int,
  molybdeniumMined: Int,
  colonistClans: Int,
  supplies: Int,
  money: Int,
  netroniumGround: Int,
  tritaniumGround: Int,
  duraniumGround: Int,
  molybdeniumGround: Int,
  neutroniumDensity: Short,
  tritaniumDensity: Short,
  duraniumDensity: Short,
  molybdeniumDensity: Short,
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

  private def readRecord(it: Iterator[Byte]): PlanetRecord = {
    PlanetRecord(
      WORD.read(it),
      WORD.read(it),
      SpacePaddedString(3).read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      DWORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
      WORD.read(it),
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
  }

  def read(it: Iterator[Byte]): IndexedSeq[PlanetRecord] = {
    val recordsNum = WORD.read(it)
    for(i <- 0 until recordsNum) yield readRecord(it)
  }
}
