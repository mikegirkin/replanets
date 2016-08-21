package replanets.ui.actions

import replanets.model._
import replanets.ui.viewmodels.ViewModel

class Actions(game: Game, viewModel: ViewModel)(
  val selectStarbase: SelectBase,
  val selectPlanet: SelectPlanet,
  val showBuildShipView: () => Unit,
  val showMapView: () => Unit,

  //orders
  val setFcode: SetFcode
) {

  private def fireSelectedObjectChanged() = {
    viewModel.selectedObject.foreach(x =>
      viewModel.objectChanged.fire(x)
    )
  }

  def buildShip(starbase: Starbase, buildOrder: ShipBuildOrder) = {
    val command = new StartShipConstruction(starbase.id, buildOrder)
    game.addCommand(command)
    fireSelectedObjectChanged()
  }

  def stopShipConstruction(starbase: Starbase) = {
    val command = StopShipConstruction(starbase.id)
    game.addCommand(command)
    fireSelectedObjectChanged()
  }
}
