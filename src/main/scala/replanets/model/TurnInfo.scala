package replanets.model

import replanets.common._
import replanets.model.commands._
import replanets.model.commands.v0.PlayerCommand

import scala.collection.mutable

case class TurnInfo(
  specs: Specs,
  initialState: GameState
) {

  private var _finalState: GameState = initialState

  def finalState: GameState = _finalState

  def applyCommand(command: PlayerCommand) = {
    _finalState = command.apply(_finalState, specs)
  }

  def getStarbaseState(baseId: PlanetId): Starbase = {
    finalState.bases(baseId)
  }

  def getStarbaseInitial(baseId: PlanetId): Starbase = {
    initialState.bases(baseId)
  }

  def getPlanetState(planetId: PlanetId): Planet = {
    finalState.planets(planetId)
  }

  def getPlanetInitial(planetId: PlanetId): Planet = {
    initialState.planets(planetId)
  }
}
