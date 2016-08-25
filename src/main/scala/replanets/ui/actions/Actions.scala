package replanets.ui.actions

import replanets.common.PlanetRecord
import replanets.model._
import replanets.model.commands.{SetColonistTax, StartShipConstruction, StopShipConstruction}
import replanets.ui.MapObject
import replanets.ui.viewmodels.ViewModel

class Actions(game: Game, viewModel: ViewModel)(
  val selectStarbase: SelectBase,
  val selectPlanet: SelectPlanet,
  val showBuildShipView: () => Unit,
  val showMapView: () => Unit,

  //orders
  val setFcode: SetFcode
) {

  private def fireObjectChangedForSelectedObject() = {
    viewModel.selectedObject.foreach(x =>
      viewModel.objectChanged.fire(x)
    )
  }

  def buildShip(starbase: Starbase, buildOrder: ShipBuildOrder) = {
    val command = new StartShipConstruction(starbase.id, buildOrder)
    game.addCommand(command)
    fireObjectChangedForSelectedObject()
  }

  def stopShipConstruction(starbase: Starbase) = {
    val command = StopShipConstruction(starbase.id)
    game.addCommand(command)
    fireObjectChangedForSelectedObject()
  }

  def changeColonistTax(planet: PlanetRecord, newTax: Int) = {
    val command = SetColonistTax(planet.id, newTax)
    game.addCommand(command)
    viewModel.objectChanged.fire(MapObject.forPlanet(game.map.planets(planet.id.value - 1)))
  }
}
