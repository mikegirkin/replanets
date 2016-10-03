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
  private def fireObjectChangedForSelectedObject() = {
    viewModel.selectedObject.foreach(x =>
      viewModel.objectChanged.fire(x)
    )
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

  //ships
  def setShipFcode(ship: OwnShip, newFcode: Fcode) = {
    val command = SetShipFcode(ship.id, newFcode)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(ship))
  }

  def setShipWarp(ship: OwnShip, newWarp: Int) = {
    val command = SetShipWarp(ship.id, newWarp)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(ship))
  }

  def shipToOwnPlanetTransfer(ship: OwnShip, planet: Planet, transfer: Cargo) = {
    val command = ShipToOwnPlanetTransfer(ship.id, planet.id, transfer)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(ship))
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  def shipToOtherPlanetTransfer(ship: OwnShip, planetId: PlanetId, transfer: Cargo) = {
    val command = ShipToOtherPlanetTransfer(ship.id, planetId, transfer)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(ship))
    val planetMapInfo = game.specs.map.planets(planetId.value - 1)
    viewModel.objectChanged.fire(MapObject.Planet(planetId.value, planetMapInfo.coords, planetMapInfo.name))
  }

  def shipToOwnShipTransfer(source: OwnShip, target: OwnShip, transfer: Cargo) = {
    val command = ShipToOwnShipTransfer(source.id, target.id, transfer)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(source))
    viewModel.objectChanged.fire(MapObject.forShip(target))
  }

  def shipToOtherShipTransfer(source: OwnShip, targetId: ShipId, transfer: Cargo) = {
    val command = ShipToOtherShipTransfer(source.id, targetId, transfer)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(source))
    game.turnInfo(viewModel.turnShown).stateAfterCommands.ships.get(targetId).foreach { x =>
      viewModel.objectChanged.fire(MapObject.forShip(x))
    }
  }

  def setPrimaryEnemy(ship: OwnShip, enemyRaceId: Option[RaceId]): Unit = {
    val command = SetPrimaryEnemy(ship.id, enemyRaceId)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(ship))
  }

  def setMission(ship: OwnShip, missionId: Int): Unit = {
    val command = SetMission(ship.id, missionId)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forShip(ship))
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

}
