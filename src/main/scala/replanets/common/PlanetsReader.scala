package replanets.common

import replanets.model.{ClusterMap, PlanetMapData}
import replanets.recipes._

case class Planet(
  ownerId: RaceId,
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
)

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