package replanets.common

import replanets.model.{Cargo, CargoHold, ClusterMap, PlanetMapData}
import replanets.recipes._

case class Planet(
  owner: RaceId,
  id: PlanetId,
  mapData: PlanetMapData,
  fcode: Fcode,
  minesNumber: Int,
  factoriesNumber: Int,
  defencesNumber: Int,
  surfaceMinerals: Minerals,
  colonistClans: Int,
  supplies: Int,
  money: Int,
  coreMinerals: Minerals,
  densityMinerals: Minerals,
  colonistTax: Int,
  nativeTax: Int,
  colonistHappiness: Short,
  nativeHappiness: Short,
  nativeGovernment: NativeGovernment,
  nativeClans: Int,
  nativeRace: NativeRace,
  temperature: Short,
  buildBase: Short
) {
  def cargoHold = CargoHold(
    Int.MaxValue,
    Int.MaxValue,
    Int.MaxValue,
    Cargo(
      surfaceMinerals.neutronium,
      surfaceMinerals.tritanium,
      surfaceMinerals.duranium,
      surfaceMinerals.molybdenium,
      supplies,
      colonistClans,
      money,
      0
    )
  )

  def hasEnoughFor(cost: Cost) = {
    surfaceMinerals.tritanium >= cost.tri &&
      surfaceMinerals.duranium >= cost.dur &&
      surfaceMinerals.molybdenium >= cost.mol &&
      supplies + money >= cost.money
  }
}

object PlanetsReader {

  def read(it: Iterator[Byte], clusterMap: ClusterMap): Map[PlanetId, Planet] = {
    val recordsNum = WORD.read(it)
    (1 to recordsNum).map { idx =>
      val raceId = RaceId(WORD.read(it))
      val planetId = PlanetId(WORD.read(it))
      planetId -> Planet(
        raceId,
        planetId,
        clusterMap.planets(planetId.value - 1),
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
    }
  }.toMap
}