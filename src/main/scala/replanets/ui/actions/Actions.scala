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
  private def fireShipChanged(ship: Ship) = {
    viewModel.objectChanged.fire(MapObject.forShip(ship))
  }

  private def fireObjectChanged(starbase: Starbase) = {
    viewModel.objectChanged.fire(MapObject.forStarbase(starbase))
  }

  private def fireObjectChanged(planet: Planet) = {
    viewModel.objectChanged.fire(MapObject.forPlanet(planet))
  }

  //Starbase
  def buildShip(starbase: Starbase, buildOrder: ShipBuildOrder) = {
    val command = new StartShipConstruction(starbase.id, buildOrder)
    game.addCommand(command)
    fireObjectChanged(starbase.planet)
    fireObjectChanged(starbase)
  }

  def stopShipConstruction(starbase: Starbase) = {
    val command = StopShipConstruction(starbase.id)
    game.addCommand(command)
    fireObjectChanged(starbase.planet)
    fireObjectChanged(starbase)
  }

  def setPrimaryOrder(starbase: Starbase, newOrderId: Int) = {
    val command = ChangeBasePrimaryOrder(starbase.id, newOrderId)
    game.addCommand(command)
    fireObjectChanged(starbase)
  }

  def baseBuildDefences(starbase: Starbase, delta: Int) = {
    val command = BaseBuildDefences(starbase.id, delta)
    game.addCommand(command)
    fireObjectChanged(starbase.planet)
    fireObjectChanged(starbase)
  }

  def baseBuildFighters(starbase: Starbase, delta: Int): Unit = {
    val command = BaseBuildFighters(starbase.id, delta)
    game.addCommand(command)
    fireObjectChanged(starbase.planet)
    fireObjectChanged(starbase)
  }

  def baseSetTechLevels(starbase: Starbase, hullsTech: Int, enginesTech: Int, beamsTech: Int, torpsTech: Int): Unit = {
    val command = BaseSetTechLevels(starbase.id, hullsTech, enginesTech, beamsTech, torpsTech)
    game.addCommand(command)
    fireObjectChanged(starbase.planet)
    fireObjectChanged(starbase)
  }

  //ships
  def setShipFcode(ship: OwnShip, newFcode: Fcode) = {
    val command = SetShipFcode(ship.id, newFcode)
    game.addCommand(command)
    fireShipChanged(ship)
  }

  def setShipWarp(ship: OwnShip, newWarp: Int) = {
    val command = SetShipWarp(ship.id, newWarp)
    game.addCommand(command)
    fireShipChanged(ship)
  }

  def shipToOwnPlanetTransfer(ship: OwnShip, planet: Planet, transfer: Cargo) = {
    val command = ShipToOwnPlanetTransfer(ship.id, planet.id, transfer)
    game.addCommand(command)
    fireShipChanged(ship)
    fireObjectChanged(planet)
  }

  def shipToOtherPlanetTransfer(ship: OwnShip, planetId: PlanetId, transfer: Cargo) = {
    val command = ShipToOtherPlanetTransfer(ship.id, planetId, transfer)
    game.addCommand(command)
    fireShipChanged(ship)
    val planetMapInfo = game.specs.map.planets(planetId.value - 1)
    viewModel.objectChanged.fire(MapObject.Planet(planetId.value, planetMapInfo.coords, planetMapInfo.name))
  }

  def shipToOwnShipTransfer(source: OwnShip, target: OwnShip, transfer: Cargo) = {
    val command = ShipToOwnShipTransfer(source.id, target.id, transfer)
    game.addCommand(command)
    fireShipChanged(source)
    fireShipChanged(target)
  }

  def shipToOtherShipTransfer(source: OwnShip, targetId: ShipId, transfer: Cargo) = {
    val command = ShipToOtherShipTransfer(source.id, targetId, transfer)
    game.addCommand(command)
    fireShipChanged(source)
    game.turnInfo(viewModel.turnShown).stateAfterCommands.ships.get(targetId).foreach { x =>
      fireShipChanged(x)
    }
  }

  def setPrimaryEnemy(ship: OwnShip, enemyRaceId: Option[RaceId]): Unit = {
    val command = SetPrimaryEnemy(ship.id, enemyRaceId)
    game.addCommand(command)
    fireShipChanged(ship)
  }

  def setMission(ship: OwnShip, missionId: Int, towArgument: Int = 0, interceptArgument: Int = 0): Unit = {
    val command = SetMission(ship.id, missionId, towArgument, interceptArgument)
    game.addCommand(command)
    fireShipChanged(ship)
  }

  def setShipName(ship: OwnShip, newName: String): Unit = {
    val command = SetShipName(ship.id, newName)
    game.addCommand(command)
    fireShipChanged(ship)
  }

  //planets
  def setPlanetFcode(planet: Planet, newFcode: Fcode) = {
    val command = SetPlanetFcode(planet.id, newFcode)
    game.addCommand(command)
    fireObjectChanged(planet)
  }

  def changeColonistTax(planet: Planet, newTax: Int) = {
    val command = SetColonistTax(planet.id, newTax)
    game.addCommand(command)
    fireObjectChanged(planet)
  }

  def changeNativeTax(planet: Planet, newTax: Int) = {
    game.addCommand(SetNativeTax(planet.id, newTax))
    fireObjectChanged(planet)
  }

  def buildFactories(planet: Planet, newFactoriesNumber: Int): Unit = {
    val ti = game.turnInfo(game.lastTurn)
    val totalFactoriesToBuild = newFactoriesNumber - ti.initialState.planets(planet.id).factoriesNumber
    game.addCommand(BuildFactories(planet.id, totalFactoriesToBuild))
    fireObjectChanged(planet)
  }

  def buildMines(planet: Planet, newMinesNumber: Int): Unit = {
    val ti = game.turnInfo(game.lastTurn)
    val totalMinesToBuild = newMinesNumber - ti.initialState.planets(planet.id).minesNumber
    game.addCommand(BuildMines(planet.id, totalMinesToBuild))
    fireObjectChanged(planet)
  }

  def buildDefences(planet: Planet, newDefencesNumber: Int): Unit = {
    val ti = game.turnInfo(game.lastTurn)
    val totalDefencesToBuild = newDefencesNumber - ti.initialState.planets(planet.id).defencesNumber
    game.addCommand(BuildDefences(planet.id, totalDefencesToBuild))
    fireObjectChanged(planet)
  }

  def buildStarbase(planet: Planet): Unit = {
    game.addCommand(BuildStarbase(planet.id))
    fireObjectChanged(planet)
  }

  def cancelBuildStarbase(planet: Planet): Unit = {
    game.addCommand(CancelBuildStarbase(planet.id))
    fireObjectChanged(planet)
  }
}
