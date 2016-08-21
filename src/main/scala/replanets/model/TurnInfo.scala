package replanets.model

import replanets.common.{PlanetId, ServerData, Starbase}

import scala.collection.mutable

case class TurnInfo(
  rst: ServerData,
  commands: mutable.Buffer[PlayerCommand]
) {
  def getStarbaseState(baseId: PlanetId): Starbase = {
    rst.bases(baseId)
  }
}
