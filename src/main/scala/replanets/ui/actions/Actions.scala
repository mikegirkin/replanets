package replanets.ui.actions

import replanets.common.{Fcode, OwnShip, Planet}
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
    viewModel.objectChanged.fire(MapObject.forStarbase(game)(starbase))
    viewModel.objectChanged.fire(MapObject.forPlanet(starbase.planet))
  }

  def stopShipConstruction(starbase: Starbase) = {
    val command = StopShipConstruction(starbase.id)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forStarbase(game)(starbase))
    viewModel.objectChanged.fire(MapObject.forPlanet(starbase.planet))
  }

  //ships
  def setShipFcode(ship: OwnShip, newFcode: Fcode) = {
    val command = SetShipFcode(ship.id, newFcode)
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
