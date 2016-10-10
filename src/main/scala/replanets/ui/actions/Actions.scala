package replanets.ui.actions

import replanets.common._
import replanets.model._
import replanets.model.commands._
import replanets.ui.MapObject
import replanets.ui.viewmodels.ViewModel

class Actions(game: Game, viewModel: ViewModel)(
  val selectStarbase: SelectBase,
  val selectPlanet: SelectPlanet,
  val showBuildShipView: () => Unit,
  val showMapView: () => Unit
) {
  private def fireShipChanged(shipId: ShipId) = {
    viewModel.objectChanged.fire(MapObject.forShip(
      game.turnInfo(game.lastTurn).stateAfterCommands.ships(shipId)
    ))
  }

  //Starbase
  def buildShip(starbase: Starbase, buildOrder: ShipBuildOrder) = {
    val command = new StartShipConstruction(starbase.id, buildOrder)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forStarbase(starbase))
    viewModel.objectChanged.fire(MapObject.forPlanet(starbase.planet))
  }

  def stopShipConstruction(starbase: Starbase) = {
    val command = StopShipConstruction(starbase.id)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forStarbase(starbase))
    viewModel.objectChanged.fire(MapObject.forPlanet(starbase.planet))
  }

  def setPrimaryOrder(starbase: Starbase, newOrderId: Int) = {
    val command = ChangeBasePrimaryOrder(starbase.id, newOrderId)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forStarbase(starbase))
  }

  def baseBuildDefences(starbase: Starbase, delta: Int) = {
    val command = BaseBuildDefences(starbase.id, delta)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forPlanet(starbase.planet))
    viewModel.objectChanged.fire(MapObject.forStarbase(starbase))
  }

  def baseBuildFighters(starbase: Starbase, delta: Int): Unit = {
    val command = BaseBuildFighters(starbase.id, delta)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forPlanet(starbase.planet))
    viewModel.objectChanged.fire(MapObject.forStarbase(starbase))
  }


  //ships
  def setShipFcode(ship: OwnShip, newFcode: Fcode) = {
    val command = SetShipFcode(ship.id, newFcode)
    game.addCommand(command)
    fireShipChanged(ship.id)
  }

  def setShipWarp(ship: OwnShip, newWarp: Int) = {
    val command = SetShipWarp(ship.id, newWarp)
    game.addCommand(command)
    fireShipChanged(ship.id)
  }

  def shipToOwnPlanetTransfer(ship: OwnShip, planet: Planet, transfer: Cargo) = {
    val command = ShipToOwnPlanetTransfer(ship.id, planet.id, transfer)
    game.addCommand(command)
    fireShipChanged(ship.id)
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def shipToOtherPlanetTransfer(ship: OwnShip, planetId: PlanetId, transfer: Cargo) = {
    val command = ShipToOtherPlanetTransfer(ship.id, planetId, transfer)
    game.addCommand(command)
    fireShipChanged(ship.id)
    val planetMapInfo = game.specs.map.planets(planetId.value - 1)
    viewModel.objectChanged.fire(MapObject.Planet(planetId.value, planetMapInfo.coords, planetMapInfo.name))
  }

  def shipToOwnShipTransfer(source: OwnShip, target: OwnShip, transfer: Cargo) = {
    val command = ShipToOwnShipTransfer(source.id, target.id, transfer)
    game.addCommand(command)
    fireShipChanged(source.id)
    fireShipChanged(target.id)
  }

  def shipToOtherShipTransfer(source: OwnShip, targetId: ShipId, transfer: Cargo) = {
    val command = ShipToOtherShipTransfer(source.id, targetId, transfer)
    game.addCommand(command)
    fireShipChanged(source.id)
    game.turnInfo(viewModel.turnShown).stateAfterCommands.ships.get(targetId).foreach { x =>
      fireShipChanged(x.id)
    }
  }

  def setPrimaryEnemy(ship: OwnShip, enemyRaceId: Option[RaceId]): Unit = {
    val command = SetPrimaryEnemy(ship.id, enemyRaceId)
    game.addCommand(command)
    fireShipChanged(ship.id)
  }

  def setMission(ship: OwnShip, missionId: Int, towArgument: Int = 0, interceptArgument: Int = 0): Unit = {
    val command = SetMission(ship.id, missionId, towArgument, interceptArgument)
    game.addCommand(command)
    fireShipChanged(ship.id)
  }

  def setShipName(ship: OwnShip, newName: String): Unit = {
    val command = SetShipName(ship.id, newName)
    game.addCommand(command)
    fireShipChanged(ship.id)
  }

  //planets
  def setPlanetFcode(planet: Planet, newFcode: Fcode) = {
    val command = SetPlanetFcode(planet.id, newFcode)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def changeColonistTax(planet: Planet, newTax: Int) = {
    val command = SetColonistTax(planet.id, newTax)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def changeNativeTax(planet: Planet, newTax: Int) = {
    game.addCommand(SetNativeTax(planet.id, newTax))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def buildFactories(planet: Planet, newFactoriesNumber: Int): Unit = {
    val ti = game.turnInfo(game.lastTurn)
    val totalFactoriesToBuild = newFactoriesNumber - ti.initialState.planets(planet.id).factoriesNumber
    game.addCommand(BuildFactories(planet.id, totalFactoriesToBuild))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def buildMines(planet: Planet, newMinesNumber: Int): Unit = {
    val ti = game.turnInfo(game.lastTurn)
    val totalMinesToBuild = newMinesNumber - ti.initialState.planets(planet.id).minesNumber
    game.addCommand(BuildMines(planet.id, totalMinesToBuild))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def buildDefences(planet: Planet, newDefencesNumber: Int): Unit = {
    val ti = game.turnInfo(game.lastTurn)
    val totalDefencesToBuild = newDefencesNumber - ti.initialState.planets(planet.id).defencesNumber
    game.addCommand(BuildDefences(planet.id, totalDefencesToBuild))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def buildStarbase(planet: Planet): Unit = {
    game.addCommand(BuildStarbase(planet.id))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def cancelBuildStarbase(planet: Planet): Unit = {
    game.addCommand(CancelBuildStarbase(planet.id))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }
}
