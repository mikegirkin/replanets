package replanets.ui.actions

import replanets.model.Game
import replanets.ui.MapObject
import replanets.ui.viewmodels.ViewModel

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
      planet <- game.specs.map.planets.find(p => p.x == so.coords.x && p.y == so.coords.y)
    ) viewModel.selectedObject = Some(MapObject.forPlanet(planet))
  }
}
