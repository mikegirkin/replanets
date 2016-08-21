package replanets.model

import replanets.common.{PlanetId, ServerData, Starbase}

import scala.collection.mutable

case class TurnInfo(
  rst: ServerData,
  commands: mutable.Buffer[PlayerCommand]
) {
  def getStarbaseState(baseId: PlanetId)(specs: Specs): Starbase = {
    commands.foldLeft(getStarbaseInitial(baseId))((base, command) => base.applyCommand(command)(specs))
  }

  def getStarbaseInitial(baseId: PlanetId): Starbase = {
    rst.bases(baseId)
  }
}
