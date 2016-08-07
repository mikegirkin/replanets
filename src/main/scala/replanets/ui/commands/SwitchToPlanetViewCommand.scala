package replanets.ui.commands

import replanets.model.Game
import replanets.ui.{IntCoords, MapObject, MapObjectType, ViewModel}

/**
  * Created by mgirkin on 07/08/2016.
  */
class SwitchToPlanetViewCommand(
  game: Game,
  viewModel: ViewModel
) extends Command {
  override def execute(): Unit = {
    for(
      so <- viewModel.objectSelected;
      planet <- game.map.planets.find(p => p.x == so.coords.x && p.y == so.coords.y)
    ) viewModel.objectSelected = Some(MapObject(MapObjectType.Planet, planet.id, IntCoords(planet.x, planet.y) ))
  }
}
