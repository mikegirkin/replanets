package replanets.model

import replanets.common._

import scala.collection.mutable

case class TurnInfo(
  initialState: ServerData,
  commands: mutable.Buffer[PlayerCommand]
) {

  def stateAfterCommands(specs: Specs): ServerData = {
    commands.foldLeft(initialState)((state, command) => {
      command match {
        case SetPlanetFcode(planetId, newFcode) => handleSetPlanetFCode(planetId, newFcode)(state)
        case SetShipFcode(shipId, newFcode) => handleSetShipFcode(shipId, newFcode)(state)
        case _ => state
      }
    })
  }

  private def handleSetPlanetFCode(planetId: PlanetId, newFCode: Fcode)(state: ServerData): ServerData = {
    state.copy(
      planets = state.planets.updated(planetId, state.planets(planetId).copy(fcode = newFCode))
    )
  }

  private def handleSetShipFcode(shipId: ShipId, newFcode: Fcode)(state: ServerData): ServerData = {
    val ship = state.ships(shipId)
    if(!ship.isInstanceOf[OwnShip]) state
    else {
      val ownShip = ship.asInstanceOf[OwnShip]
      state.copy(
        ships = state.ships.updated(shipId, ownShip.copy(fcode = newFcode))
      )
    }
  }

  def getStarbaseState(baseId: PlanetId)(specs: Specs): Starbase = {
    commands.foldLeft(getStarbaseInitial(baseId))((base, command) => base.applyCommand(command)(specs))
  }

  def getStarbaseInitial(baseId: PlanetId): Starbase = {
    initialState.bases(baseId)
  }

  def getPlanetState(planetId: PlanetId): PlanetRecord = {
    getPlanetInitial(planetId)
  }

  def getPlanetInitial(planetId: PlanetId): PlanetRecord = {
    initialState.planets(planetId)
  }
}
