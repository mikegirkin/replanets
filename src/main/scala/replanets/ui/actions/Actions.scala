package replanets.ui.actions

import replanets.common.{ShipBuildOrder, Starbase}
import replanets.model.{BuildShip, Game}
import replanets.ui.viewmodels.ViewModel

class Actions(game: Game, viewModel: ViewModel)(
  val selectStarbase: SelectBase,
  val selectPlanet: SelectPlanet,
  val showBuildShipView: () => Unit,
  val showMapView: () => Unit,

  //orders
  val setFcode: SetFcode
) {

  val buildShip = (starbase: Starbase, buildOrder: ShipBuildOrder) => {
    val command = BuildShip(starbase.id, buildOrder)
    game.addCommand(command)
    viewModel.selectedObject.foreach(x =>
      viewModel.objectChanged.fire(x)
    )
  }

}
