package replanets.model.commands.v0

import replanets.common.NumberExtensions._
import replanets.common.{Cost, GameState, LauncherId, PlanetId}
import replanets.model.{CalcUtils, Specs, TurnInfo}

case class StarbaseBuySellTorpedoes(
  baseId: PlanetId,
  torpedoType: LauncherId,
  delta: Int
) {

  def apply(turnInfo: TurnInfo, specs: Specs): GameState = {
    val starbaseInitial = turnInfo.getStarbaseInitial(baseId)
    val starbaseCurrent = turnInfo.getStarbaseState(baseId)

    // Toprs built = (Sum(orbiting ships) + starbase storage - initially on starbase)
    val initialTorpsOnShips = turnInfo.initialState.getOwnShipAtCoords(starbaseInitial.planet.mapData.coords)
      .filter(s => s.torpsType.exists(_.id == torpedoType))
      .map(_.torpsFightersLoaded)
      .sum

    val initialTorps = initialTorpsOnShips + starbaseInitial.storedTorpedoes(torpedoType)

    val torpsOnShipsNow = turnInfo.finalState.getOwnShipAtCoords(starbaseInitial.planet.mapData.coords)
      .filter(s => s.torpsType.exists(_.id == torpedoType))
      .map(_.torpsFightersLoaded)
      .sum

    val torpsNow = torpsOnShipsNow + starbaseCurrent.storedTorpedoes(torpedoType)

    //We could "sell" only torps that we actually build this turn
    val torpsCouldBeSold = Seq(torpsNow - initialTorps, starbaseCurrent.storedTorpedoes(torpedoType)).min

    //Max torps to build is resources / cost
    val torpsCouldBeBuild = {
      for {
        laucherSpec <- specs.torpSpecs.find(_.id == torpedoType)
        planet = turnInfo.finalState.planets(baseId)
      } yield CalcUtils.maxPossibleUnitsForGivenCost(
        planet.surfaceMinerals, planet.money, planet.supplies, Cost(1, 1, 1, laucherSpec.torpedoMoneyCost))
    }.getOrElse(0)

    val torpsToBuild = delta.bounded(-torpsCouldBeSold, torpsCouldBeBuild)
  }

}
