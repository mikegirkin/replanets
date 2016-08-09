package replanets.ui.actions

import replanets.model.Game
import replanets.ui.viewmodels.ViewModel
import replanets.ui.{IntCoords, MapObject, MapObjectType}

/**
  * Created by mgirkin on 07/08/2016.
  */
class SelectPlanet(
  game: Game,
  viewModel: ViewModel
) extends UserAction {
  override def execute(): Unit = {
    for(
      so <- viewModel.selectedObject;
      planet <- game.map.planets.find(p => p.x == so.coords.x && p.y == so.coords.y)
    ) viewModel.selectedObject = Some(MapObject(MapObjectType.Planet, planet.id, IntCoords(planet.x, planet.y) ))
  }
}
