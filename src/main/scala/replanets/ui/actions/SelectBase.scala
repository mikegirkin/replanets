package replanets.ui.actions

import replanets.model.Game
import replanets.ui.viewmodels.ViewModel
import replanets.ui.{MapObject, MapObjectType}

class SelectBase(
  game: Game,
  viewModel: ViewModel
) extends UserAction {
  override def execute(): Unit = {
    for(
      so <- viewModel.selectedObject;
      planet <- game.map.planets.find(p => p.x == so.coords.x && p.y == so.coords.y);
      base <- game.turnSeverData(viewModel.turnShown).bases.find(b => b.baseId == planet.id)
    ) {
      if(base.owner == game.playingRace.value) viewModel.selectedObject = Some(MapObject(MapObjectType.Base, base.baseId, so.coords))
    }
  }
}